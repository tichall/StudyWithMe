package december.spring.studywithme.controller;


import december.spring.studywithme.dto.*;
import december.spring.studywithme.exception.UserException;
import december.spring.studywithme.jwt.JwtUtil;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 1. 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.createUser(requestDTO);

        ResponseMessage<UserResponseDTO> responseMessage = ResponseMessage.<UserResponseDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("회원가입이 완료되었습니다.")
                .data(responseDTO)
                .build();

        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    /**
     * 2. 회원 탈퇴
     */
    @PutMapping("/withdraw")
    public ResponseEntity<ResponseMessage<String>> withdrawUser(@Valid @RequestBody PasswordRequestDTO requestDTO,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userId = userService.withdrawUser(requestDTO, userDetails.getUser());

        ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("회원 탈퇴가 완료되었습니다.")
                .data(userId)
                .build();

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    //로그아웃
    @GetMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.logout(userDetails.getUser());

        return ResponseEntity.ok(ResponseMessage.builder()
                .statusCode(HttpStatus.OK.value())
                .message("로그아웃이 완료되었습니다.")
                .build());
    }


    /**
     * 프로필 조회 (개인)
     * 인증된 사용자의 정보를 조회하여 반환
     *
     * @param userDetails 현재 인증된 사용자의 정보를 담고 있는 UserDetailsImpl 객체
     * @return 사용자 정보를 담은 UserProfileResponseDTO 객체
     */
    @GetMapping("/mypage")
    public ResponseEntity<ResponseMessage<UserProfileResponseDTO>> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserProfileResponseDTO userInfoResponseDTO = userService.inquiryUser(userDetails.getUsername());
        return ResponseEntity.ok(ResponseMessage.<UserProfileResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .data(userInfoResponseDTO)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> getProfileById(@PathVariable Long id) {
        UserResponseDTO userResponseDTO = userService.inquiryUserById(id);
        return ResponseEntity.ok(ResponseMessage.<UserResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .data(userResponseDTO)
                .build());
    }

    @PutMapping("/mypage")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> updateUser(@Valid @RequestBody UserProfileUpateRequestDTO requestDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponseDTO userResponseDTO = userService.updateProfile(requestDTO, userDetails.getUser());
        ResponseMessage<UserResponseDTO> responseMessage = ResponseMessage.<UserResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("프로필 수정이 완료되었습니다.")
                .data(userResponseDTO)
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
//
//    @PutMapping("/password")
//
//    String editPassword = requestDTO.getNewPassword() != null ? passwordEncoder.encode(requestDTO.getNewPassword()) : user.getPassword();
//
//            if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), user.getPassword())) {
//        throw new UserException("비밀번호가 일치하지 않습니다.");
//    }
//        if (passwordEncoder.matches(requestDTO.getNewPassword(), user.getPassword())) {
//        throw new UserException("새로운 비밀번호와 기존 비밀번호가 동일합니다.");
//}