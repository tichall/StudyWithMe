package december.spring.studywithme.service;

import december.spring.studywithme.dto.PostRequestDto;
import december.spring.studywithme.dto.PostResponseDto;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.security.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import december.spring.studywithme.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
	private final PostRepository postRepository;
	@Transactional
	public PostResponseDto createPost(UserDetailsImpl userDetails, PostRequestDto request) {
		Post post = Post.builder()
				.title(request.getTitle())
				.contents(request.getContents())
				.user(userDetails.getUser())
				.build();
		Post savePost = postRepository.save(post);
		return new PostResponseDto(savePost);
	}
}
