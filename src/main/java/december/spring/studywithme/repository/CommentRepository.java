package december.spring.studywithme.repository;

import december.spring.studywithme.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    Optional<Comment> findByPostIdAndId(Long postId, Long commentId);
}
