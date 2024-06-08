package december.spring.studywithme.repository;

import december.spring.studywithme.entity.ContentsType;
import december.spring.studywithme.entity.Like;
import december.spring.studywithme.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Like findByUserAndTargetIdAndContentsType(User user, Long targetId, ContentsType contentsType);

    @Query(value = "select count(*) from Like like " +
    "where like.targetId = :targetId and like.contentsType = :contentsType and like.isLike = true")
    Long countByTargetIdAndContentsTypeAndIsLike(@Param("targetId") Long targetId, @Param("contentsType") ContentsType contentsType);
}
