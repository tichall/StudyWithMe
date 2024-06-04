package december.spring.studywithme.controller;

import org.springframework.web.bind.annotation.RestController;

import december.spring.studywithme.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
}
