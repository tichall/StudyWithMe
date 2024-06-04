package december.spring.studywithme.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import december.spring.studywithme.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
	private final PostRepository postRepository;
}
