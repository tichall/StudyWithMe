package december.spring.studywithme.controller;

import december.spring.studywithme.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
	public ResponseEntity<ResponseMessage> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request){

		String accessToken = jwtUtil.getJwtFromHeader(request);
		String refreshToken = jwtUtil.getJwtRefreshTokenFromHeader(request);
		userService.logout(userDetails.getUser(), accessToken, refreshToken);

		return ResponseEntity.ok(ResponseMessage.builder()
			.statusCode(HttpStatus.OK.value())
			.message("로그아웃이 완료되었습니다.")
			.build());
	}
}
