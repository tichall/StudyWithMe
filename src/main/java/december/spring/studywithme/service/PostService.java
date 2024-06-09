package december.spring.studywithme.service;


import december.spring.studywithme.dto.PostRequestDTO;
import december.spring.studywithme.dto.PostResponseDTO;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.exception.NoContentException;
import december.spring.studywithme.exception.PostException;
import december.spring.studywithme.repository.PostRepository;
import december.spring.studywithme.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * 3. 게시글 전체 조회
	 * @return PostResponseDTO 게시글 전체 조회 결과
	 */
	public List<PostResponseDTO> getAllPost() {
		List<Post> postList = postRepository.findAllByOrderByCreateAtDesc();
		
		if (postList.isEmpty()) {
			throw new NoContentException("먼저 작성하여 소식을 알려보세요!");
		}
		
		return postList.stream().map(PostResponseDTO::new).toList();
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
		postRepository.flush();
		
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
}
