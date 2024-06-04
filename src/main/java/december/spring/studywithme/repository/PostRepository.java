package december.spring.studywithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import december.spring.studywithme.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
