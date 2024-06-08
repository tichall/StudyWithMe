package december.spring.studywithme.service;

import december.spring.studywithme.entity.*;
import december.spring.studywithme.exception.LikeException;
import december.spring.studywithme.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;

    /**
     * 1. 게시글 좋아요 등록 / 취소
     * @param postId 게시글 ID
     * @param user 로그인한 사용자 정보
     * @return 좋아요 등록 / 취소 여부
     */
    @Transactional
    public boolean likePost(Long postId, User user) {
        Post post = postService.getValidatePost(postId);

        if (post.getUser().getUserId().equals(user.getUserId())) {
            throw new LikeException("본인이 작성한 게시글에는 좋아요를 남길 수 없습니다.");
        }

        boolean result = toggleLike(user, post.getId(), ContentsType.POST);
        updateLikes(post);
        return result;
    }

    /**
     * 2. 댓글 좋아요 등록 / 취소
     * @param postId 게시글 ID
     * @param commentId 댓글 ID
     * @param user 로그인한 사용자 정보
     * @return 좋아요 등록 / 취소 여부
     */
    @Transactional
    public boolean likeComment(Long postId, Long commentId, User user) {
        Post post = postService.getValidatePost(postId);
        Comment comment = commentService.getValidateComment(post.getId(), commentId);

        if (comment.getUser().getUserId().equals(user.getUserId())) {
            throw new LikeException("본인이 작성한 댓글에는 좋아요를 남길 수 없습니다.");
        }

        boolean result = toggleLike(user, comment.getId(), ContentsType.COMMENT);
        updateLikes(comment);
        return result;
    }

    /**
     * DB에 좋아요 등록
     * @param user 로그인한 사용자 정보
     * @param targetId 좋아요 대상 ID
     * @param contentsType 좋아요 대상 타입
     * @return 좋아요 등록 여부
     */
    public boolean toggleLike(User user, Long targetId, ContentsType contentsType) {
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

    /**
     * 게시글 좋아요 수 업데이트
     * @param post 게시글
     */
    private void updateLikes(Post post) {
        Long countLikes = likeRepository.countByTargetIdAndContentsTypeAndIsLike(post.getId(), ContentsType.POST);
        post.updatePostLikes(countLikes);
    }

    /**
     * 댓글 좋아요 수 업데이트
     * @param comment 댓글
     */
    private void updateLikes(Comment comment) {
        Long countLikes = likeRepository.countByTargetIdAndContentsTypeAndIsLike(comment.getId(), ContentsType.COMMENT);
        comment.updateCommentLikes(countLikes);
    }
}
