package december.spring.studywithme.controller;

import december.spring.studywithme.dto.CommentRequestDTO;
import december.spring.studywithme.dto.CommentResponseDTO;
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

    /**
     * 1. 댓글 등록
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param postId 게시물의 ID
     * @param requestDto 댓글 생성 요청 데이터
     * @return ResponseEntity<ResponseMessage<CommentResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     *         - 상태 코드: 댓글이 성공적으로 생성되면 201 (CREATED)
     *         - 메시지: 댓글 생성 상태를 설명하는 메시지
     *         - 데이터: 생성된 댓글의 정보를 담고 있는 CommentResponseDTO 객체
     */
    @PostMapping
    public ResponseEntity<ResponseMessage<CommentResponseDTO>> createComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long postId, @Valid @RequestBody CommentRequestDTO requestDto) {
        CommentResponseDTO responseDto = commentService.createComment(userDetails, postId, requestDto);

        ResponseMessage<CommentResponseDTO> responseMessage = ResponseMessage.<CommentResponseDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("댓글 등록이 완료되었습니다.")
                .data(responseDto)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);

    }

    /**
     * 2. 전체 댓글 조회
     * @param postId 게시물의 ID
     * @return ResponseEntity<ResponseMessage<List<CommentResponseDTO>>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     *        - 상태 코드: 댓글 조회가 성공적으로 이루어지면 200 (OK)
     *        - 메시지: 댓글 조회 상태를 설명하는 메시지
     *        - 데이터: 조회된 댓글 목록을 담고 있는 CommentResponseDTO 객체의 리스트
     */
    @GetMapping
    public ResponseEntity<ResponseMessage<List<CommentResponseDTO>>> getAllComments(@PathVariable Long postId) {
        List<CommentResponseDTO> responseDtoList = commentService.getAllComments(postId);

        ResponseMessage<List<CommentResponseDTO>> responseMessage = ResponseMessage.<List<CommentResponseDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("전체 댓글 조회가 완료되었습니다.")
                .data(responseDtoList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    /**
     * 3. 단일 댓글 조회
     * @param postId 게시물의 ID
     * @param commentId 댓글의 ID
     * @return ResponseEntity<ResponseMessage<CommentResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     *       - 상태 코드: 댓글 조회가 성공적으로 이루어지면 200 (OK)
     *       - 메시지: 댓글 조회 상태를 설명하는 메시지
     *       - 데이터: 조회된 댓글의 정보를 담고 있는 CommentResponseDTO 객체
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseMessage<CommentResponseDTO>> getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        CommentResponseDTO responseDto = commentService.getComment(postId, commentId);

        ResponseMessage<CommentResponseDTO> responseMessage = ResponseMessage.<CommentResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("선택한 댓글 조회가 완료되었습니다.")
                .data(responseDto)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    /**
     * 4. 댓글 수정
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param postId 게시물의 ID
     * @param commentId 댓글의 ID
     * @param requestDto 댓글 수정 요청 데이터
     * @return ResponseEntity<ResponseMessage<CommentResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     *       - 상태 코드: 댓글 수정이 성공적으로 이루어지면 200 (OK)
     *       - 메시지: 댓글 수정 상태를 설명하는 메시지
     *       - 데이터: 수정된 댓글의 정보를 담고 있는 CommentResponseDTO 객체
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseMessage<CommentResponseDTO>> updateComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long postId, @PathVariable Long commentId, @Valid @RequestBody CommentRequestDTO requestDto) {
        CommentResponseDTO responseDto = commentService.updateComment(userDetails, postId, commentId, requestDto);

        ResponseMessage<CommentResponseDTO> responseMessage = ResponseMessage.<CommentResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("댓글 수정이 완료되었습니다.")
                .data(responseDto)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    /**
     * 5. 댓글 삭제
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param postId 게시물의 ID
     * @param commentId 댓글의 ID
     * @return ResponseEntity<ResponseMessage<Long>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     *      - 상태 코드: 댓글 삭제가 성공적으로 이루어지면 200 (OK)
     *      - 메시지: 댓글 삭제 상태를 설명하는 메시지
     *      - 데이터: 삭제된 댓글의 ID
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseMessage<Long>> deleteComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(userDetails, postId, commentId);

        ResponseMessage<Long> responseMessage = ResponseMessage.<Long>builder()
                .statusCode(HttpStatus.OK.value())
                .message("댓글 삭제가 완료되었습니다.")
                .data(commentId)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

}
