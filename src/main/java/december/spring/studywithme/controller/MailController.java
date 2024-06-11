package december.spring.studywithme.controller;

import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import december.spring.studywithme.dto.CertificationNumberRequestDTO;
import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.MailService;
import december.spring.studywithme.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mails")
public class MailController {
	private final MailService mailSendService;
	private final UserService userService;
	
	/**
	 * 1. 이메일 인증 코드 발송
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @return ResponseEntity<ResponseMessage<String>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 이메일 인증 코드가 정상적으로 발송되면 200 (OK)
	 * 	   - 메시지: "인증 코드 발송이 완료되었습니다."
	 * 	   - 데이터: 이메일 주소
	 * @throws MessagingException
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<ResponseMessage<String>> sendCertificationNumber(@AuthenticationPrincipal UserDetailsImpl userDetails)
			throws MessagingException, NoSuchAlgorithmException {
		String email = mailSendService.sendEmailForCertification(userDetails.getUser().getEmail());
		
		ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
			.statusCode(HttpStatus.OK.value())
			.message("인증 코드 발송이 완료되었습니다.")
			.data(email)
			.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}

	/**
	 * 2. 이메일 인증 코드 확인
	 * @param requestDTO 인증 코드 확인 요청 데이터
	 * @param userDetails 로그인한 사용자의 세부 정보
	 * @return ResponseEntity<ResponseMessage<String>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 이메일 인증이 정상적으로 완료되면 200 (OK)
	 * 	   - 메시지: "이메일 인증이 완료되었습니다."
	 * 	   - 데이터: 이메일 주소
	 */
	@GetMapping
	public ResponseEntity<ResponseMessage<String>> verifyCertificationNumber(@Valid @RequestBody CertificationNumberRequestDTO requestDTO,
																			 @AuthenticationPrincipal UserDetailsImpl userDetails) {
		mailSendService.verifyEmail(userDetails.getUser().getEmail(), requestDTO.getCode());
		userService.updateUserActive(userDetails.getUser());
		
		ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
			.statusCode(HttpStatus.OK.value())
			.message("이메일 인증이 완료되었습니다.")
			.data(userDetails.getUser().getEmail())
			.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}
		
}
