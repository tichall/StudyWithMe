package december.spring.studywithme.service;

import december.spring.studywithme.dto.PostResponseDto;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.exception.NoPostException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import december.spring.studywithme.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
	private final PostRepository postRepository;

	public List<PostResponseDto> getAllPost() {
		List<Post> postList = postRepository.findAllByOrderByCreateAtDesc();

		if (postList.isEmpty()) {
			throw new NoPostException("먼저 작성하여 소식을 알려보세요!");
		}

		return postList.stream().map(PostResponseDto::new).toList();
	}
}
