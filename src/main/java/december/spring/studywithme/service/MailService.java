package december.spring.studywithme.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import december.spring.studywithme.exception.EmailException;
import december.spring.studywithme.repository.CertificationNumberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
	private final JavaMailSender mailSender;
	private final CertificationNumberRepository certificationNumberRepository;
	public static final String MAIL_TITLE_CERTIFICATION = "STUDY WITH ME 이메일 인증";

	/**
	 * 1. 이메일 인증 코드 발송
	 * @param email 이메일 주소
	 * @return 이메일 주소
	 * @throws NoSuchAlgorithmException
	 * @throws MessagingException
	 */
	public String sendEmailForCertification(String email) throws NoSuchAlgorithmException, MessagingException {
		String certificationNumber = createCertificationNumber();
		String content = String.format("인증 번호 : " + certificationNumber + "\n인증코드를 3분 이내에 입력해주세요.");
		certificationNumberRepository.saveCertificationNumber(email, certificationNumber);
		sendMail(email, content);
		return email;
	}

	/**
	 * 2. 이메일 발송
	 * @param email 이메일 주소
	 * @param content 이메일 내용
	 * @throws MessagingException
	 */
	private void sendMail(String email, String content) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		helper.setTo(email);
		helper.setSubject(MAIL_TITLE_CERTIFICATION);
		helper.setText(content);
		mailSender.send(mimeMessage);
	}

	/**
	 * 3. 이메일 인증 확인
	 * @param email 이메일 주소
	 * @param certificationNumber 인증 번호
	 */
	public void verifyEmail(String email, String certificationNumber) {
		if (! certificationNumberRepository.getCertificationNumber(email).equals(certificationNumber)) {
			throw new EmailException("인증번호가 일치하지 않습니다.");
		}
		
		certificationNumberRepository.removeCertificationNumber(email);
	}

	/**
	 * 인증 번호 생성
	 * @return 인증 번호
	 * @throws NoSuchAlgorithmException
	 */
	public String createCertificationNumber() throws NoSuchAlgorithmException {
		String result;
		
		do {
			int num = SecureRandom.getInstanceStrong().nextInt(999999);
			result = String.valueOf(num);
		} while (result.length() != 6);
		
		return result;
	}
}
