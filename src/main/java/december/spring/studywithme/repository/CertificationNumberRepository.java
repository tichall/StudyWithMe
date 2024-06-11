package december.spring.studywithme.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CertificationNumberRepository {
	private final StringRedisTemplate redisTemplate;
	static final int EMAIL_VERIFICATION_LIMIT_IN_SECONDS = 180;

	/**
	 * 인증코드 저장
	 * @param email 이메일 주소
	 * @param certificationNumber 인증 코드
	 */
	public void saveCertificationNumber(String email, String certificationNumber) {
		redisTemplate.opsForValue()
			.set(email, certificationNumber, Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));
	}

	/**
	 * 인증코드 조회
	 * @param email 이메일 주소
	 * @return 인증 코드
	 */
	public String getCertificationNumber(String email) {
		return redisTemplate.opsForValue().get(email);
	}

	/**
	 * 인증코드 삭제
	 * @param email 이메일 주소
	 */
	public void removeCertificationNumber(String email) {
		redisTemplate.delete(email);
	}
}
