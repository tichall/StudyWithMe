package december.spring.studywithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import december.spring.studywithme.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
