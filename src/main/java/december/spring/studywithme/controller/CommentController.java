package december.spring.studywithme.controller;

import december.spring.studywithme.dto.CommentRequestDto;
import december.spring.studywithme.dto.CommentResponseDto;
import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseMessage<CommentResponseDto>> createComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long postId, @Valid @RequestBody CommentRequestDto requestDto) {
        CommentResponseDto responseDto = commentService.createComment(userDetails, postId, requestDto);

        ResponseMessage<CommentResponseDto> responseMessage = ResponseMessage.<CommentResponseDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("댓글 등록이 완료되었습니다.")
                .data(responseDto)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseMessage);

    }

    @GetMapping
    public ResponseEntity<ResponseMessage<List<CommentResponseDto>>> getAllComments(@PathVariable Long postId) {
        List<CommentResponseDto> responseDtoList = commentService.getAllComments(postId);

        ResponseMessage<List<CommentResponseDto>> responseMessage = ResponseMessage.<List<CommentResponseDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("전체 댓글 조회가 완료되었습니다.")
                .data(responseDtoList)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseMessage);
    }

}
