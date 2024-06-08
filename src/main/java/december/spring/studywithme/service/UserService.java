package december.spring.studywithme.service;

import java.time.LocalDateTime;
import java.util.Optional;

import december.spring.studywithme.dto.*;
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
     * 1. 회원 가입
     * @param requestDTO 회원 가입 요청 데이터
     * @return UserResponseDTO 회원 가입 결과
     */
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        //아이디 유효성 검사
        validateUserId(requestDTO.getUserId());

        //이메일 유효성 검사
        validateUserEmail(requestDTO.getEmail());

        //비밀번호 암호화
        String password = passwordEncoder.encode(requestDTO.getPassword());
        User user = User.builder()
                .userId(requestDTO.getUserId())
                .password(password)
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .userType(UserType.UNVERIFIED)
                .introduce(requestDTO.getIntroduce())
                .statusChangedAt(LocalDateTime.now())
                .build();

        User saveUser = userRepository.save(user);

        return new UserResponseDTO(saveUser);
    }

    /**
     * 2. 회원 활성화
     * @param user 활성화할 회원
     */
    @Transactional
    public void updateUserActive(User user) {
        user.ActiveUser();
        userRepository.save(user);
    }

    /**
     * 3. 회원 탈퇴
     * @param requestDTO 비밀번호 확인 요청 데이터
     * @param user 로그인한 사용자의 세부 정보
     * @return 탈퇴된 회원의 ID
     */
    @Transactional
    public String withdrawUser(PasswordRequestDTO requestDTO, User user) {
        //회원 상태 확인
        checkUserType(user.getUserType());

        //비밀번호 일치 확인
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new UserException("비밀번호가 일치하지 않습니다.");
        }

        //회원 상태 변경
        user.withdrawUser();
        userRepository.save(user);

        return user.getUserId();
    }

    /**
     * 4. 로그아웃
     * @param user 로그인한 사용자의 세부 정보
     * @param accessToken access token
     * @param refreshToken refresh token
     */
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

    /**
     * 5. 회원 조회
     * @param userId 조회할 회원의 ID
     * @return UserProfileResponseDTO 회원 조회 결과
     */
    public UserProfileResponseDTO inquiryUser(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserException("해당 유저를 찾을 수 없습니다."));
        return new UserProfileResponseDTO(user);
    }

    /**
     * 6. 회원 프로필 수정
     * @param requestDTO 프로필 수정 요청 데이터
     * @param user 로그인한 사용자의 세부 정보
     * @return UserResponseDTO 회원 프로필 수정 결과
     */
    @Transactional
    public UserResponseDTO updateProfile(UserProfileUpateRequestDTO requestDTO, User user) {

        if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), user.getPassword())) {
            throw new UserException("비밀번호가 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(requestDTO.getNewPassword(), user.getPassword())) {
            throw new UserException("새로운 비밀번호와 기존 비밀번호가 동일합니다.");
        }

        user.editProfile(requestDTO.getName(), passwordEncoder.encode(requestDTO.getNewPassword()), requestDTO.getIntroduce());

        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    /**
     * 아이디 유효성 검사
     * @param id 아이디
     */
    private void validateUserId(String id) {
        Optional<User> findUser = userRepository.findByUserId(id);
        if (findUser.isPresent()) {
            throw new UserException("중복된 id 입니다.");
        }
    }

    /**
     * 이메일 유효성 검사
     * @param email 이메일
     */
    private void validateUserEmail(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        if(findUser.isPresent()) {
            throw new UserException("중복된 Email 입니다.");
        }
    }

    /**
     * 회원 상태 확인
     * @param userType 회원 상태
     */
    private void checkUserType(UserType userType) {
        if (userType.equals(UserType.DEACTIVATED)) {
            throw new UserException("이미 탈퇴한 회원입니다.");
        }
    }

}
