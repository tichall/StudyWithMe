package december.spring.studywithme.controller;

import december.spring.studywithme.dto.PostRequestDto;
import december.spring.studywithme.dto.PostResponseDto;
import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import december.spring.studywithme.service.PostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	@PostMapping
	public ResponseEntity<ResponseMessage<PostResponseDto>> createPost(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody PostRequestDto request) {
		PostResponseDto postResponseDto = postService.createPost(userDetails, request);
		ResponseMessage<PostResponseDto> responseMessage = ResponseMessage.<PostResponseDto>builder()
				.statusCode(HttpStatus.CREATED.value())
				.message("게시물 생성 완료")
				.data(postResponseDto)
				.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseMessage<PostResponseDto>> getPost(@PathVariable Long id) {
		PostResponseDto postResponseDto = postService.getPost(id);
		ResponseMessage<PostResponseDto> responseMessage = ResponseMessage.<PostResponseDto>builder()
				.statusCode(HttpStatus.OK.value())
				.message("게시물 조회 완료")
				.data(postResponseDto)
				.build();
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
}
