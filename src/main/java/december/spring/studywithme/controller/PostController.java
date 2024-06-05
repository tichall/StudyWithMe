package december.spring.studywithme.controller;

import december.spring.studywithme.dto.PostRequestDto;
import december.spring.studywithme.dto.PostResponseDto;
import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import december.spring.studywithme.service.PostService;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
				.message("생성 완료")
				.data(postResponseDto)
				.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}

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

	@PutMapping("/{id}")
	public ResponseEntity<ResponseMessage<PostResponseDto>> updatePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody PostRequestDto requestDto) {
		PostResponseDto responseDto = postService.updatePost(id, userDetails, requestDto);

		ResponseMessage<PostResponseDto> responseMessage = ResponseMessage.<PostResponseDto>builder()
				.statusCode(HttpStatus.OK.value())
				.message("게시글 수정이 완료되었습니다.")
				.data(responseDto)
				.build();

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(responseMessage);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseMessage<Void>> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		postService.deletePost(id, userDetails);

		ResponseMessage<Void> responseMessage = ResponseMessage.<Void>builder()
				.statusCode(HttpStatus.OK.value())
				.message("게시글 삭제가 완료되었습니다.")
				.build();

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(responseMessage);
	}
}
