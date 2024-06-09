package december.spring.studywithme.controller;

import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

	/**
	 * 1. 게시글 좋아요 등록 / 취소
	 * @param postId 게시글 ID
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @return ResponseEntity<ResponseMessage<Long>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 좋아요가 성공적으로 등록되면 200 (OK)
	 * 	   			 좋아요가 성공적으로 취소되면 200 (OK)
	 * 	   			 (게시글 좋아요 등록 / 취소에 따라 다름)
	 * 	   - 메시지: 좋아요 상태를 설명하는 메시지
	 * 	   			 게시글 좋아요 등록: "게시글 좋아요. 등록"
	 * 	   			 게시글 좋아요 취소: "게시글 좋아요. 취소"
	 * 	   			 (게시글 좋아요 등록 / 취소에 따라 다름)
	 *	   - 데이터: 게시글 ID
	 */
	@PostMapping("/{postId}/like")
	public ResponseEntity<ResponseMessage<Long>> likePost(@PathVariable Long postId,
														  @AuthenticationPrincipal UserDetailsImpl userDetails){

		boolean check = likeService.likePost(postId, userDetails.getUser());
		String message = check ? "게시글 좋아요. 등록" : "게시글 좋아요. 취소";

		ResponseMessage<Long> responseMessage = ResponseMessage.<Long>builder()
				.statusCode(HttpStatus.OK.value())
				.message(message)
				.data(postId)
				.build();

		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}

	/**
	 * 2. 댓글 좋아요 등록 / 취소
	 * @param postId 게시글 ID
	 * @param commentId 댓글 ID
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @return ResponseEntity<ResponseMessage<Long>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	  	- 상태 코드: 좋아요가 성공적으로 등록되면 200 (OK)
	 * 	   			 좋아요가 성공적으로 취소되면 200 (OK)
	 * 	   			 (댓글 좋아요 등록 / 취소에 따라 다름)
	 *		- 메시지: 좋아요 상태를 설명하는 메시지
	 *			 	댓글 좋아요 등록: "댓글 좋아요. 등록"
	 *			 	댓글 좋아요 취소: "댓글 좋아요. 취소"
	 *			 	(댓글 좋아요 등록 / 취소에 따라 다름)
	 *		- 데이터: 댓글 ID
	 */
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<ResponseMessage<Long>> likeComment(@PathVariable Long postId, @PathVariable Long commentId,
															 @AuthenticationPrincipal UserDetailsImpl userDetails){

		boolean check = likeService.likeComment(postId, commentId, userDetails.getUser());
		String message = check ? "댓글 좋아요. 등록" : "댓글 좋아요. 취소";

		ResponseMessage<Long> responseMessage = ResponseMessage.<Long>builder()
				.statusCode(HttpStatus.OK.value())
				.message(message)
				.data(commentId)
				.build();

		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }
}
