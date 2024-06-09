package december.spring.studywithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import december.spring.studywithme.entity.Post;
import december.spring.studywithme.entity.PostLike;
import december.spring.studywithme.entity.User;
import io.lettuce.core.dynamic.annotation.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByUserAndPost(User user, Post post);

    @Query("SELECT COUNT(p1) FROM PostLike p1 WHERE p1.post = :post AND p1.isLike = true")
    Long countByPostAndIsLike(@Param("post") Post post);
}
