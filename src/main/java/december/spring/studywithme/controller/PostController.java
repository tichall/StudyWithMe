package december.spring.studywithme.controller;

import december.spring.studywithme.dto.PostResponseDto;
import december.spring.studywithme.dto.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import december.spring.studywithme.service.PostService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@GetMapping
	public ResponseEntity<ResponseMessage<List<PostResponseDto>>> getAllPost() {
		List<PostResponseDto> responseDtoList = postService.getAllPost();
		ResponseMessage<List<PostResponseDto>> responseMessage = ResponseMessage.<List<PostResponseDto>>builder()
				.statusCode(HttpStatus.OK.value())
				.message("전체 게시글 조회가 완료되었습니다.")
				.data(responseDtoList)
				.build();

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(responseMessage);
	}

}
