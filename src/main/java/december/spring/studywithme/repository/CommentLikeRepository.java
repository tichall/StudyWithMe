package december.spring.studywithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import december.spring.studywithme.entity.Comment;
import december.spring.studywithme.entity.CommentLike;
import december.spring.studywithme.entity.User;
import io.lettuce.core.dynamic.annotation.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByUserAndComment(User user, Comment comment);

    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment = :comment AND cl.isLike = true")
    Long countByCommentAndIsLike(@Param("comment") Comment comment);
}
