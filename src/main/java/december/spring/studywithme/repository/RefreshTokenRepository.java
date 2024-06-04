package december.spring.studywithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import december.spring.studywithme.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
