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
}
