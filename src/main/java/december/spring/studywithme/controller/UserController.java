package december.spring.studywithme.controller;

import december.spring.studywithme.dto.UserInfoResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import december.spring.studywithme.dto.PasswordRequestDTO;
import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.dto.UserRequestDTO;
import december.spring.studywithme.dto.UserResponseDTO;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

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
	 * @param userDetails 현재 인증된 사용자의 정보를 담고 있는 UserDetailsImpl 객체
	 * @return 사용자 정보를 담은 UserInfoResponseDTO 객체
	 */
	@GetMapping("/mypage")
	public ResponseEntity<UserInfoResponseDTO> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		String userId = userDetails.getUsername();
		UserInfoResponseDTO userInfoResponseDTO = userService.inquiryUser(userId);
		return ResponseEntity.ok(userInfoResponseDTO);
	}
}