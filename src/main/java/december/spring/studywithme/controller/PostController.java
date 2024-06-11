package december.spring.studywithme.controller;

import december.spring.studywithme.dto.PostPageResponseDTO;
import december.spring.studywithme.dto.PostRequestDTO;
import december.spring.studywithme.dto.PostResponseDTO;
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

    /**
     * 1. 게시글 등록
     *
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param request     게시글 생성 요청 데이터
     * @return ResponseEntity<ResponseMessage<PostResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     * - 상태 코드: 게시글이 성공적으로 생성되면 201 (CREATED)
     * - 메시지: 게시글 생성 상태를 설명하는 메시지
     * - 데이터: 생성된 게시글의 정보를 담고 있는 PostResponseDTO 객체
     */
    @PostMapping
    public ResponseEntity<ResponseMessage<PostResponseDTO>> createPost(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody PostRequestDTO request) {
        PostResponseDTO postResponseDTO = postService.createPost(userDetails, request);

        ResponseMessage<PostResponseDTO> responseMessage = ResponseMessage.<PostResponseDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("게시글 생성이 완료되었습니다.")
                .data(postResponseDTO)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    /**
     * 2. 단일 게시글 조회
     *
     * @param id 게시글의 ID
     * @return ResponseEntity<ResponseMessage<PostResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     * - 상태 코드: 게시글 조회가 성공적으로 이루어지면 200 (OK)
     * - 메시지: 게시글 조회 상태를 설명하는 메시지
     * - 데이터: 조회된 게시글의 정보를 담고 있는 PostResponseDTO 객체
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<PostResponseDTO>> getPost(@PathVariable Long id) {
        PostResponseDTO postResponseDTO = postService.getPost(id);

        ResponseMessage<PostResponseDTO> responseMessage = ResponseMessage.<PostResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("게시글 조회가 완료되었습니다.")
                .data(postResponseDTO)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    /**
     * 3. 전체 게시글 페이지 조회
     *
     * @param page 접근할 페이지
     * @param sortBy 게시글 정렬 기준
     * @param from 기간 시작 일자
     * @param to 기간 마지막 일자
     * @return ResponseEntity<ResponseMessage<PostPageResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     * - 상태 코드: 게시글 조회가 성공적으로 이루어지면 200 (OK)
     * - 메시지: 게시글 조회 상태를 설명하는 메시지
     * - 데이터: 조회된 페이지와 게시글의 정보를 담고 있는 PostPageResponseDTO 객체
     */
    @GetMapping
    public ResponseEntity<ResponseMessage<PostPageResponseDTO>> getPostPage(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to
    ) {
        PostPageResponseDTO pageResponseDto = postService.getPostPage(page, sortBy, from, to);

        ResponseMessage<PostPageResponseDTO> responseMessage = ResponseMessage.<PostPageResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("게시글 페이지 조회가 완료되었습니다.")
                .data(pageResponseDto)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    /**
     * 4. 게시글 수정
     *
     * @param id          게시글의 ID
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param requestDto  게시글 수정 요청 데이터
     * @return ResponseEntity<ResponseMessage<PostResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage<PostResponseDTO>> updatePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody PostRequestDTO requestDto) {
        PostResponseDTO responseDTO = postService.updatePost(id, userDetails, requestDto);

        ResponseMessage<PostResponseDTO> responseMessage = ResponseMessage.<PostResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("게시글 수정이 완료되었습니다.")
                .data(responseDTO)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    /**
     * 5. 게시글 삭제
     *
     * @param id          게시글의 ID
     * @param userDetails 로그인한 사용자의 세부 정보
     * @return ResponseEntity<ResponseMessage<Long>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
     * - 상태 코드: 게시글 삭제가 성공적으로 이루어지면 200 (OK)
     * - 메시지: 게시글 삭제 상태를 설명하는 메시지
     * - 데이터: 삭제된 게시글의 ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage<Long>> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(id, userDetails);

        ResponseMessage<Long> responseMessage = ResponseMessage.<Long>builder()
                .statusCode(HttpStatus.OK.value())
                .message("게시글 삭제가 완료되었습니다.")
                .data(id)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

}
