package december.spring.studywithme.repository;

import december.spring.studywithme.entity.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByUserAndPost(User user, Post post);

    @Query("SELECT COUNT(p1) FROM PostLike p1 WHERE p1.post = :post AND p1.isLike = true")
    Long countByPostAndIsLike(Post post);
}
