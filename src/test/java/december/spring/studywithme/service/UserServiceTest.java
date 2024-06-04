package december.spring.studywithme.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import december.spring.studywithme.dto.PasswordRequestDTO;
import december.spring.studywithme.dto.UserRequestDTO;
import december.spring.studywithme.dto.UserResponseDTO;
import december.spring.studywithme.entity.User;
import december.spring.studywithme.exception.UserException;
import december.spring.studywithme.repository.UserRepository;

@SpringBootTest
class UserServiceTest {
	@Autowired
	UserService userService;
	@Autowired
	private UserRepository userRepository;

	void createUsers() {
		UserRequestDTO user1 = new UserRequestDTO();
		user1.setUserId("heesue12345");
		user1.setPassword("Hihello1234!");
		user1.setName("희수");
		user1.setEmail("36-96@gmail.com");
		user1.setIntroduce("hi~");
		userService.createUser(user1);
		
		UserRequestDTO user2 = new UserRequestDTO();
		user2.setUserId("abcdef1234");
		user2.setPassword("Hihello1234!");
		user2.setName("이희수");
		user2.setEmail("12345@naver.com");
		user2.setIntroduce("hi~");
		userService.createUser(user2);
		
		UserRequestDTO user3 = new UserRequestDTO();
		user3.setUserId("helloworld9876");
		user3.setPassword("Hihello1234!");
		user3.setName("헬로월드");
		user3.setEmail("helloworld@naver.com");
		user3.setIntroduce("hi~");
		userService.createUser(user3);
	}
	
	@Test
	@Transactional
	@DisplayName("유저 생성 성공 테스트")
	void 유저생성성공() {
	    //given
	    UserRequestDTO requestDTO = new UserRequestDTO();
		requestDTO.setUserId("testUser111");
		requestDTO.setPassword("Hihello1234!");
		requestDTO.setName("희수");
		requestDTO.setEmail("kh010101@naver.com");
		requestDTO.setIntroduce("공부하실 분 구합니다.");
		
	    //when
		UserResponseDTO responseDTO = userService.createUser(requestDTO);
		
		//then
		assertThat(requestDTO.getUserId()).isEqualTo(responseDTO.getUserId());
	}
	
	@Test
	@Transactional
	@DisplayName("유저 생성 실패 테스트 - 동일한 아이디로 생성")
	void 유저생성실패_아이디중복() {
		//given
		UserRequestDTO requestDTO = new UserRequestDTO();
		requestDTO.setUserId("helloWorld12");
		requestDTO.setPassword("Hihello1234!");
		requestDTO.setName("희수");
		requestDTO.setEmail("kh0101@naver.com");
		requestDTO.setIntroduce("공부하실 분 구합니다.");
		
		//when & then
		assertThrows(UserException.class, () -> userService.createUser(requestDTO));
	}
	
	@Test
	@Transactional
	@DisplayName("유저 탈퇴 성공 테스트")
	void 유저탈퇴성공() {
	    //given
		User user = userRepository.findById(10L).orElseThrow();
		PasswordRequestDTO requestDTO = new PasswordRequestDTO();
		requestDTO.setPassword("TestPassword!123");
		
		//when
		String userId = userService.withdrawUser(requestDTO, user);
		
		//then
		assertThat(user.getUserId()).isEqualTo(userId);
	}
	
	@Test
	@Transactional
	@DisplayName("유저 탈퇴 실패 테스트 - 비밀번호 불일치")        
	void 유저탈퇴실패_비밀번호불일치() {
		//given
		User user = userRepository.findById(1L).orElseThrow();
		PasswordRequestDTO requestDTO = new PasswordRequestDTO();
		requestDTO.setPassword("TestPassword!13");
		
	    //when & then
		assertThrows(UserException.class, () -> userService.withdrawUser(requestDTO, user));
	}
	
	@Test
	@Transactional
	@DisplayName("유저 탈퇴 실패 테스트 - 이미 탈퇴한 회원")
	void 유저탈퇴실패_이미탈퇴한회원() {
	    //given
		User user = userRepository.findById(4L).orElseThrow();
		PasswordRequestDTO requestDTO = new PasswordRequestDTO();
		requestDTO.setPassword("TestPassword!123");
		
	    //when & then
		assertThrows(UserException.class, () -> userService.withdrawUser(requestDTO, user));
	}
}