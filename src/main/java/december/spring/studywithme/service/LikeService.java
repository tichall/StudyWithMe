package december.spring.studywithme.service;

import december.spring.studywithme.entity.*;
import december.spring.studywithme.exception.LikeException;
import december.spring.studywithme.repository.CommentLikeRepository;
import december.spring.studywithme.repository.LikeRepository;
import december.spring.studywithme.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    //게시글 좋아요 등록 / 취소
    @Transactional
    public boolean likePost(Long postId, User user) {
        Post post = postService.getValidatePost(postId);

        if (post.getUser().getUserId().equals(user.getUserId())) {
            throw new LikeException("본인이 작성한 게시글에는 좋아요를 남길 수 없습니다.");
        }

        boolean result = postLikeUpdate(user, post);
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

        boolean result = commentLikeUpdate(user, comment);
        updateLikes(comment);
        return result;
    }

    /**
     * 게시글 좋아요 DB 업데이트
     */
    public boolean postLikeUpdate(User user, Post post){
        PostLike postLike = postLikeRepository.findByUserAndPost(user, post);

        //like 객체 업데이트
        if (postLike != null) {
            postLike.update(!postLike.isLike());
        } else {
            postLike = PostLike.builder()
                    .user(user)
                    .post(post)
                    .isLike(true)
                    .build();
            postLikeRepository.save(postLike);
        }
        return postLike.isLike();
    }

    /**
     * 댓글 좋아요 DB 업데이트
     */
    public boolean commentLikeUpdate(User user, Comment comment){
        CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment);

        //like 객체 업데이트
        if (commentLike != null) {
            commentLike.update(!commentLike.isLike());
        } else {
            commentLike = CommentLike.builder()
                    .user(user)
                    .comment(comment)
                    .isLike(true)
                    .build();
            commentLikeRepository.save(commentLike);
        }
        return commentLike.isLike();
    }

    /**
     * 좋아요 개수 업데이트
     */
    private void updateLikes(Post post) {
        Long countLikes = postLikeRepository.countByPostAndIsLike(post);
        post.updatePostLikes(countLikes);
    }

    private void updateLikes(Comment comment) {
        Long countLikes = commentLikeRepository.countByCommentAndIsLike(comment);
        comment.updateCommentLikes(countLikes);
    }

}
