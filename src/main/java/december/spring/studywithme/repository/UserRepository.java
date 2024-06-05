package december.spring.studywithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import december.spring.studywithme.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserId(String id);
}
