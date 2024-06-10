package december.spring.studywithme.service;


import december.spring.studywithme.dto.PostPageResponseDTO;
import december.spring.studywithme.dto.PostRequestDTO;
import december.spring.studywithme.dto.PostResponseDTO;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.exception.NoContentException;
import december.spring.studywithme.exception.PageException;
import december.spring.studywithme.exception.PostException;
import december.spring.studywithme.repository.PostRepository;
import december.spring.studywithme.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
	private final PostRepository postRepository;

	/**
	 * 1. 게시글 생성
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @param request 게시글 생성 요청 데이터
	 * @return PostResponseDTO 게시글 생성 결과
	 */
	@Transactional
	public PostResponseDTO createPost(UserDetailsImpl userDetails, PostRequestDTO request) {
		Post post = Post.builder()
			.title(request.getTitle())
			.contents(request.getContents())
			.user(userDetails.getUser())
			.build();

		Post savePost = postRepository.save(post);
		return new PostResponseDTO(savePost);
	}

	/**
	 * 2. 게시글 단일 조회
	 * @param id 게시글의 ID
	 * @return PostResponseDTO 게시글 조회 결과
	 */
	public PostResponseDTO getPost(Long id) {
		Post post = getValidatePost(id);
		return new PostResponseDTO(post);
	}

	/**
	 * 3. 전체 게시글 페이지 조회
	 * @param page 접근할 페이지
	 * @param sortBy 게시글 정렬 기준
	 * @param from 기간 시작 일자
	 * @param to 기간 마지막 일자
	 * @return PostPageResponseDTO 게시글 페이지 조회 결과
	 */
	public PostPageResponseDTO getPostPage(Integer page, String sortBy, String from, String to) {
		Pageable pageable = createPageable(page, sortBy);
		Page<Post> postPage;

		try {
			if (from != null && to != null) {
				LocalDateTime startDate = parseDateString(from, true);
				LocalDateTime finishDate = parseDateString(to, false);

				if (startDate.isAfter(finishDate) || startDate.isEqual(finishDate)) {
					throw new IllegalArgumentException("기간 설정이 올바르지 않습니다.");
				}

				postPage = postRepository.findPostPageByPeriod(startDate, finishDate, pageable);

			} else if (from != null) {
				LocalDateTime startDate = parseDateString(from, true);
				postPage = postRepository.findPostPageByStartDate(startDate, pageable);
			} else if (to != null) {
				LocalDateTime finishDate = parseDateString(to, false);
				postPage = postRepository.findPostPageByFinishDate(finishDate, pageable);
			} else {
				postPage = postRepository.findAll(pageable);
			}
		} catch (DateTimeParseException e) {
		throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. yyyy-mm-dd 형식으로 입력해주세요!");
		}

		checkValidatePage(postPage, page);
		return new PostPageResponseDTO(page, postPage);
	}

	/**
	 * 4. 게시글 수정
	 * @param id 게시글의 ID
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @param requestDto 게시글 수정 요청 데이터
	 * @return PostResponseDTO 게시글 수정 결과
	 */
	@Transactional
	public PostResponseDTO updatePost(Long id, UserDetailsImpl userDetails, PostRequestDTO requestDto) {
		Post post = getValidatePost(id);
		checkPostWriter(post, userDetails);

		// 수정 진행
		post.update(requestDto);
		postRepository.save(post);

		return new PostResponseDTO(post);
	}

	/**
	 * 5. 게시글 삭제
	 * @param id 게시글의 ID
	 * @param userDetails 로그인한 사용자의 세부 정보
	 */
	@Transactional
	public void deletePost(Long id, UserDetailsImpl userDetails) {
		Post post = getValidatePost(id);
		checkPostWriter(post, userDetails);
		postRepository.delete(post);
	}

	/**
	 * 게시글 존재 여부 확인
	 * @param id 게시글 ID
	 * @return Post
	 */
	public Post getValidatePost(Long id) {
		return postRepository.findById(id).orElseThrow(() ->
			new PostException("게시글이 존재하지 않습니다."));
	}

	/**
	 * 게시글 작성자 확인
	 * @param post 게시글
	 * @param userDetails 로그인한 사용자의 세부 정보
	 */
	private void checkPostWriter(Post post, UserDetailsImpl userDetails) {
		if (!post.getUser().getUserId().equals(userDetails.getUsername())) {
			throw new PostException("작성자가 아니므로, 접근이 제한됩니다.");
		}
	}

	/**
	 * Pageable 객체 생성
	 * @param page 접근할 페이지
	 * @param sortBy 게시글 정렬 기준
	 * @return Pageable 객체
	 */
	public Pageable createPageable(int page, String sortBy) {
		return PageRequest.of(page - 1, 10, Sort.Direction.DESC, sortBy);
	}

	/**
	 * 날짜 문자열 파싱
	 * @param date 파싱할 날짜 문자열
	 * @param isStartDate 시작 일자 여부
	 * @return LocalDateTime 객체
	 */
	public LocalDateTime parseDateString(String date, boolean isStartDate) {
		LocalDate parseDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
		return isStartDate ? parseDate.atStartOfDay() : parseDate.plusDays(1).atStartOfDay();
	}

	/**
	 * 페이지 유효성 검사
	 * @param postPage 조회된 Page<Post> 객체
	 * @param page 접근할 페이지
	 */
	private void checkValidatePage(Page<Post> postPage, Integer page) {
		if (postPage.getTotalElements() == 0) {
			throw new NoContentException("게시글이 존재하지 않습니다.");
		}

		if (page < 1 || page > postPage.getTotalPages()) {
			throw new PageException("페이지가 존재하지 않습니다.");
		}
	}
}
