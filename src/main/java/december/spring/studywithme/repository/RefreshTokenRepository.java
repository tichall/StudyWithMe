package december.spring.studywithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import december.spring.studywithme.entity.RefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
