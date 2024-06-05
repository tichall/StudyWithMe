package december.spring.studywithme.service;

import java.time.LocalDateTime;
import java.util.Optional;

import december.spring.studywithme.jwt.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import december.spring.studywithme.dto.PasswordRequestDTO;
import december.spring.studywithme.dto.UserRequestDTO;
import december.spring.studywithme.dto.UserResponseDTO;
import december.spring.studywithme.entity.User;
import december.spring.studywithme.entity.UserType;
import december.spring.studywithme.exception.UserException;
import december.spring.studywithme.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	/**
	 * 1. 회원가입
	 */
	@Transactional
	public UserResponseDTO createUser(UserRequestDTO requestDTO) {
		//아이디 유효성 검사
		validateUserId(requestDTO.getUserId());
		
		//비밀번호 암호화
		String password = passwordEncoder.encode(requestDTO.getPassword());
		
		User user = User.builder()
			.userId(requestDTO.getUserId())
			.password(password)
			.name(requestDTO.getName())
			.email(requestDTO.getEmail())
			.userType(UserType.ACTIVE)
			.introduce(requestDTO.getIntroduce())
			.statusChangedAt(LocalDateTime.now())
			.build();
		
		User saveUser = userRepository.save(user);
		
		return new UserResponseDTO(saveUser);
	}
	
	/**
	 * 2. 회원탈퇴
	 */
	@Transactional
	public String withdrawUser(PasswordRequestDTO requestDTO, User user) {
		//회원 상태 확인
		checkUserType(user.getUserType());
		
		//비밀번호 일치 확인
		if(!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
			throw new UserException("비밀번호가 일치하지 않습니다.");
		}
		
		//회원 상태 변경
		user.withdrawUser();
		userRepository.save(user);
		
		return user.getUserId();
	}
	
	/**
	 * 아이디 유효성 검사
	 */
	private void validateUserId(String id) {
		Optional<User> findUser = userRepository.findByUserId(id);
		if (findUser.isPresent()) {
			throw new UserException("중복된 id 입니다.");
		}
	}
	
	/**
	 * 유저 타입 검사
	 */
	private void checkUserType(UserType userType) {
		if (userType.equals(UserType.DEACTIVATED)) {
			throw new UserException("이미 탈퇴한 회원입니다.");
		}
	}

	//로그아웃
	@Transactional
	public void logout(User user, String accessToken, String refreshToken) {

		if(user==null){
			throw new UserException("로그인되어 있는 유저가 아닙니다.");
		}

		if(user.getUserType().equals(UserType.DEACTIVATED)){
			throw new UserException("탈퇴한 회원입니다.");
		}

		User existingUser = userRepository.findByUserId(user.getUserId())
						.orElseThrow(() -> new UserException("해당 유저가 존재하지 않습니다."));

		existingUser.refreshTokenReset("");
		userRepository.save(existingUser);

		jwtUtil.invalidateToken(accessToken);
		jwtUtil.invalidateToken(refreshToken);
	}

}
