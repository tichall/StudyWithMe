package december.spring.studywithme.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import december.spring.studywithme.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
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
	 * 아이디 유효성 검사
	 */
	private void validateUserId(String id) {
		Optional<User> findUser = userRepository.findByUserId(id);
		if (findUser.isPresent()) {
			throw new UserException("중복된 id 입니다.");
		}
	}
}
