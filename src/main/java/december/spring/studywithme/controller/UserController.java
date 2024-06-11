package december.spring.studywithme.controller;

import december.spring.studywithme.dto.*;
import december.spring.studywithme.jwt.JwtUtil;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
	 * 1. 회원 가입
	 * @param requestDTO 회원 가입 요청 데이터
	 * @return ResponseEntity<ResponseMessage<UserResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 회원 가입이 성공적으로 이루어지면 201 (CREATED)
	 * 	   - 메시지: 회원 가입 상태를 설명하는 메시지
	 * 	   - 데이터: 생성된 회원의 정보를 담고 있는 UserResponseDTO 객체
	 */
	@PostMapping("/signup")
	public ResponseEntity<ResponseMessage<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
		UserResponseDTO responseDTO = userService.createUser(requestDTO);
		
		ResponseMessage<UserResponseDTO> responseMessage = ResponseMessage.<UserResponseDTO>builder()
			.statusCode(HttpStatus.CREATED.value())
			.message("회원가입이 완료되었습니다.")
			.data(responseDTO)
			.build();
		
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}
	
	/**
	 * 2. 회원 탈퇴
	 * @param requestDTO 비밀번호 확인 요청 데이터
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @return ResponseEntity<ResponseMessage<String>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 회원 탈퇴가 성공적으로 이루어지면 200 (OK)
	 * 	   - 메시지: 회원 탈퇴 상태를 설명하는 메시지
	 * 	   - 데이터: 탈퇴된 회원의 ID
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
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
	
	/**
	 * 3. 로그아웃
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @param request HTTP 요청
	 * @return ResponseEntity<ResponseMessage<String>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 로그아웃이 성공적으로 이루어지면 200 (OK)
	 * 	   - 메시지: 로그아웃 상태를 설명하는 메시지
	 * 	   - 데이터: 로그아웃된 회원의 ID
	 */
	@GetMapping("/logout")
	public ResponseEntity<ResponseMessage<String>> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request){
		
		String accessToken = jwtUtil.getJwtFromHeader(request);
		String refreshToken = jwtUtil.getJwtRefreshTokenFromHeader(request);
		userService.logout(userDetails.getUser(), accessToken, refreshToken);
		
		ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
			.statusCode(HttpStatus.OK.value())
			.message("로그아웃이 완료되었습니다.")
			.data(userDetails.getUser().getUserId())
			.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
	
	/**
	 * 4. 회원 정보 조회
	 * @param id 조회할 회원의 ID
	 * @return ResponseEntity<ResponseMessage<UserResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 회원 조회가 성공적으로 이루어지면 200 (OK)
	 * 	   - 메시지: 회원 조회 상태를 설명하는 메시지
	 * 	   - 데이터: 조회된 회원의 정보를 담고 있는 UserResponseDTO 객체
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ResponseMessage<UserResponseDTO>> getUserProfileById(@PathVariable Long id) {
		UserResponseDTO userResponseDTO = userService.inquiryUserById(id);
		
		ResponseMessage<UserResponseDTO> responseMessage = ResponseMessage.<UserResponseDTO>builder()
			.statusCode(HttpStatus.OK.value())
			.message("프로필 조회가 완료되었습니다.")
			.data(userResponseDTO)
			.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
	
	/**
	 * 5. 로그인한 사용자 프로필 조회
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @return ResponseEntity<ResponseMessage<UserProfileResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 프로필 조회가 성공적으로 이루어지면 200 (OK)
	 * 	   - 메시지: 프로필 조회 상태를 설명하는 메시지
	 * 	   - 데이터: 조회된 사용자의 정보를 담고 있는 UserProfileResponseDTO 객체
	 */
	@GetMapping("/mypage")
	public ResponseEntity<ResponseMessage<UserProfileResponseDTO>> userProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		UserProfileResponseDTO responseDTO = userService.inquiryUser(userDetails.getUsername());
		
		ResponseMessage<UserProfileResponseDTO> responseMessage = ResponseMessage.<UserProfileResponseDTO>builder()
			.statusCode(HttpStatus.OK.value())
			.message("프로필 조회가 완료되었습니다.")
			.data(responseDTO)
			.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
	
	/**
	 * 6. 로그인한 사용자 프로필 수정
	 * @param requestDTO 프로필 수정 요청 데이터
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @return ResponseEntity<ResponseMessage<UserResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 프로필 수정이 성공적으로 이루어지면 200 (OK)
	 * 	   - 메시지: 프로필 수정 상태를 설명하는 메시지
	 * 	   - 데이터: 수정된 사용자의 정보를 담고 있는 UserResponseDTO 객체
	 */
	@PutMapping("/mypage")
	public ResponseEntity<ResponseMessage<UserResponseDTO>> updateUser(@RequestBody UserProfileUpdateRequestDTO requestDTO,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		UserResponseDTO userResponseDTO = userService.editProfile(requestDTO, userDetails.getUser());
		
		ResponseMessage<UserResponseDTO> responseMessage = ResponseMessage.<UserResponseDTO>builder()
			.statusCode(HttpStatus.OK.value())
			.message("프로필 수정이 완료되었습니다.")
			.data(userResponseDTO)
			.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
	
	/**
	 * 7. 비밀번호 변경
	 * @param requestDTO 비밀번호 변경 요청 데이터
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @return ResponseEntity<ResponseMessage<UserResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 비밀번호 변경이 성공적으로 이루어지면 200 (OK)
	 * 	   - 메시지: 비밀번호 변경 상태를 설명하는 메시지
	 * 	   - 데이터: 변경된 사용자의 정보를 담고 있는 UserResponseDTO 객체
	 */
	@PutMapping("/password")
	public ResponseEntity<ResponseMessage<UserResponseDTO>> updatePassword(@Valid @RequestBody EditPasswordRequestDTO requestDTO,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		UserResponseDTO userResponseDTO = userService.editPassword(requestDTO, userDetails);
		
		ResponseMessage<UserResponseDTO> responseMessage = ResponseMessage.<UserResponseDTO>builder()
			.statusCode(HttpStatus.OK.value())
			.message("비밀번호가 변경되었습니다.")
			.data(userResponseDTO)
			.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
}
