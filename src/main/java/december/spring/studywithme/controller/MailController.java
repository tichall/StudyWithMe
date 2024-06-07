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
	 * 이메일 인증 번호 요청
	 */
	@PostMapping
	public ResponseEntity<ResponseMessage<String>> sendCertificationNumber(@AuthenticationPrincipal UserDetailsImpl userDetails) throws MessagingException, NoSuchAlgorithmException {
		String email = mailSendService.sendEmailForCertification(userDetails.getUser().getEmail());
		
		ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
			.statusCode(HttpStatus.OK.value())
			.message("인증 코드 발송이 완료되었습니다.")
			.data(email)
			.build();
		
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
	
	/**
	 * 이메일 인증
	 */
	@GetMapping
	public ResponseEntity<ResponseMessage<String>> verifyCertificationNumber(@Valid @RequestBody CertificationNumberRequestDTO requestDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		mailSendService.verifyEmail(userDetails.getUser().getEmail(), requestDTO.getCode());
		userService.updateUserActive(userDetails.getUser());
		
		ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
			.statusCode(HttpStatus.OK.value())
			.message("이메일 인증이 완료되었습니다.")
			.data(userDetails.getUser().getEmail())
			.build();
		
		return new ResponseEntity<>(responseMessage, HttpStatus.OK);
	}
		
}
