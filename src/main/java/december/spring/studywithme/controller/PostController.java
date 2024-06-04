package december.spring.studywithme.controller;

import org.springframework.web.bind.annotation.RestController;

import december.spring.studywithme.service.PostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
}
