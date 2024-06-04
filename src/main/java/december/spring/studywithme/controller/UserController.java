package december.spring.studywithme.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		
		ResponseMessage<String> responseMessag = ResponseMessage.<String>builder()
			.statusCode(HttpStatus.OK.value())
			.message("회원 탈퇴가 완료되었습니다.")
			.data(userId)
			.build();
		
		return new ResponseEntity<>(responseMessag, HttpStatus.OK);
	}
}
