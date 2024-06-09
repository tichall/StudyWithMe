package december.spring.studywithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import december.spring.studywithme.entity.Post;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    Page<Post> findAll(Pageable pageable);

    @Query("select post from Post post " +
            "where post.createdAt >= :startDate and post.createdAt < :finishDate")
    Page<Post> findPostPageByPeriod(LocalDateTime startDate, LocalDateTime finishDate, Pageable pageable);
}
