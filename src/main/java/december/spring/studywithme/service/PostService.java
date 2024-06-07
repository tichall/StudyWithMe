package december.spring.studywithme.service;

import december.spring.studywithme.dto.PostRequestDto;
import december.spring.studywithme.dto.PostResponseDto;
import december.spring.studywithme.entity.ContentsType;
import december.spring.studywithme.entity.Like;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.entity.User;
import december.spring.studywithme.exception.LikeException;
import december.spring.studywithme.exception.NoPostException;
import december.spring.studywithme.exception.PostException;
import december.spring.studywithme.repository.LikeRepository;
import december.spring.studywithme.security.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import december.spring.studywithme.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;
	private final LikeRepository likeRepository;

	@Transactional
	public PostResponseDto createPost(UserDetailsImpl userDetails, PostRequestDto request) {
		Post post = Post.builder()
				.title(request.getTitle())
				.contents(request.getContents())
				.user(userDetails.getUser())
				.build();
		Post savePost = postRepository.save(post);
		return new PostResponseDto(savePost);
	}

	public PostResponseDto getPost(Long id) {
		Post post = getValidatePost(id);
		return new PostResponseDto(post);
	}

    public List<PostResponseDto> getAllPost() {
        List<Post> postList = postRepository.findAllByOrderByCreateAtDesc();

        if (postList.isEmpty()) {
            throw new NoPostException("먼저 작성하여 소식을 알려보세요!");
        }

        return postList.stream().map(PostResponseDto::new).toList();
    }

	@Transactional
	public PostResponseDto updatePost(Long id, UserDetailsImpl userDetails, PostRequestDto requestDto) {
		Post post = getValidatePost(id);
		checkPostWriter(post, userDetails);

		// 수정 진행
		post.update(requestDto);
		postRepository.flush();

		return new PostResponseDto(post);
	}

	@Transactional
	public void deletePost(Long id, UserDetailsImpl userDetails) {
		Post post = getValidatePost(id);
		checkPostWriter(post, userDetails);
		postRepository.delete(post);
	}

	/**
	 * 게시글 존재 여부 확인
	 */
	private Post getValidatePost(Long id) {
		return postRepository.findById(id).orElseThrow(() ->
				new PostException("게시글이 존재하지 않습니다."));
	}

	/**
	 * 게시글 작성자 정보 확인
	 */
	private void checkPostWriter(Post post, UserDetailsImpl userDetails) {
		if (!post.getUser().getUserId().equals(userDetails.getUsername())) {
			throw new PostException("작성자가 아니므로, 접근이 제한됩니다.");
		}
	}

	//게시글 좋아요 등록 / 취소
	@Transactional
	public boolean likePost(Long postId, User user) {

		Post post = postRepository.findById(postId).orElseThrow(() ->
				new PostException("게시글이 존재하지 않습니다."));

		if (post.getUser().getUserId().equals(user.getUserId())) {
			throw new LikeException("본인이 작성한 게시글에는 좋아요를 남길 수 없습니다.");
		}

		return toggleLike(user, ContentsType.POST, post.getId());

	}

	// 댓글 좋아요 등록 / 취소
//    @Transactional
//    public boolean likeComment(Long postId, User user) {
//
//        Comment comment = postRepository.findById(postId).orElseThrow(() ->
//                new PostException("게시글이 존재하지 않습니다."));
//
//		if (comment.getUser().getUserId().equals(user.getUserId())) {
//			throw new LikeException("본인이 작성한 게시글에는 좋아요를 남길 수 없습니다.");
//		}
//
//        return toggleLike(user, ContentsType.COMMENT, comment.getId());
//    }

	// 좋아요 DB 저장
	private boolean toggleLike(User user, ContentsType contentsType, Long targetId) {
		Like like = likeRepository.findByUserAndTargetIdAndContentsType(user, targetId, contentsType);

		//like 객체 업데이트
		if (like != null) {
			like.update(!like.isLike());
		} else {
			like = Like.builder()
					.user(user)
					.targetId(targetId)
					.contentsType(contentsType)
					.isLike(true)
					.build();
			likeRepository.save(like);
		}

		return like.isLike();
	}

}
