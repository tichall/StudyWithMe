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

    //게시글 좋아요 등록 / 취소
    @Transactional
    public boolean likePost(Long postId, User user) {
        Post post = postService.getValidatePost(postId);

        if (post.getUser().getUserId().equals(user.getUserId())) {
            throw new LikeException("본인이 작성한 게시글에는 좋아요를 남길 수 없습니다.");
        }

        boolean result = applyLike(user, ContentsType.POST, post.getId());
        updateLikes(post);
        return result;
    }

    // 댓글 좋아요 등록 / 취소
    @Transactional
    public boolean likeComment(Long postId, Long commentId, User user) {
        Post post = postService.getValidatePost(postId);
        Comment comment = commentService.getValidateComment(post.getId(), commentId);

        if (comment.getUser().getUserId().equals(user.getUserId())) {
            throw new LikeException("본인이 작성한 댓글에는 좋아요를 남길 수 없습니다.");
        }

        boolean result = applyLike(user, ContentsType.COMMENT, comment.getId());
        updateLikes(comment);
        return result;
    }

    // 좋아요 DB 저장
    public boolean applyLike(User user, ContentsType contentsType, Long targetId) {
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
     * 좋아요 개수 업데이트
     */
    private void updateLikes(Post post) {
        Long countLikes = likeRepository.countByTargetIdAndContentsTypeAndIsLike(post.getId(), ContentsType.POST);

        post.updatePostLikes(countLikes);
    }

    private void updateLikes(Comment comment) {
        Long countLikes = likeRepository.countByTargetIdAndContentsTypeAndIsLike(comment.getId(), ContentsType.COMMENT);

        comment.updateCommentLikes(countLikes);
    }
}
