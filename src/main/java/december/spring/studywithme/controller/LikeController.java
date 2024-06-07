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

    //게시글 좋아요 / 취소
	@PostMapping("/{postId}/like")
	public ResponseEntity<ResponseMessage<Long>> likePost(@PathVariable Long postId
			, @AuthenticationPrincipal UserDetailsImpl userDetails){

		if(likeService.likePost(postId, userDetails.getUser())){
			return ResponseEntity.ok(ResponseMessage.<Long>builder()
					.statusCode(HttpStatus.OK.value())
					.message("게시글 좋아요. 등록")
					.data(postId)
					.build());
		}
		else{
			return ResponseEntity.ok(ResponseMessage.<Long>builder()
					.statusCode(HttpStatus.OK.value())
					.message("게시글 좋아요. 취소")
					.data(postId)
					.build());
		}
	}

    //댓글 좋아요 / 취소
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<ResponseMessage<Long>> likeComment(@PathVariable Long postId, @PathVariable Long commentId
            , @AuthenticationPrincipal UserDetailsImpl userDetails){

        if(likeService.likeComment(postId, commentId, userDetails.getUser())){
            return ResponseEntity.ok(ResponseMessage.<Long>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("댓글 좋아요. 등록")
					.data(commentId)
                    .build());
        }
        else{
            return ResponseEntity.ok(ResponseMessage.<Long>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("댓글 좋아요. 취소")
					.data(commentId)
                    .build());
        }
    }
}
