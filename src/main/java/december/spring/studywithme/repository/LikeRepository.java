package december.spring.studywithme.repository;

import december.spring.studywithme.entity.ContentsType;
import december.spring.studywithme.entity.Like;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Like findByUserAndTargetIdAndContentsType(User user, Long targetId, ContentsType contentsType);
}
