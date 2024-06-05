package december.spring.studywithme.controller;

import december.spring.studywithme.dto.LikeRequestDto;
import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.repository.LikeRepository;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    //게시글 좋아요 / 취소
    @PostMapping("{postId}")
    public ResponseEntity<ResponseMessage<Void>> likePost(@PathVariable Long postId
                                                            , @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikeRequestDto likeRequestDto){

        if(likeService.likePost(postId, userDetails.getUser(), likeRequestDto)){
            return ResponseEntity.ok(ResponseMessage.<Void>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("좋아요. 등록")
                    .build());
        }
        else{
            return ResponseEntity.ok(ResponseMessage.<Void>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("좋아요. 취소")
                    .build());
        }
    }

    //댓글 좋아요 / 취소
    @PostMapping("{postId}/{commentId}")
    public ResponseEntity<ResponseMessage<Void>> likeComment(@PathVariable Long postId
            , @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody LikeRequestDto likeRequestDto){

        if(likeService.likeComment(postId, userDetails.getUser(), likeRequestDto)){
            return ResponseEntity.ok(ResponseMessage.<Void>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("좋아요. 등록")
                    .build());
        }
        else{
            return ResponseEntity.ok(ResponseMessage.<Void>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("좋아요. 취소")
                    .build());
        }
    }

}
