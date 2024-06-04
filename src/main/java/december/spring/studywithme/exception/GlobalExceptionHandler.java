package december.spring.studywithme.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	/*
	예시코드입니다.
	
	@ExceptionHandler(ScheduleNotFoundException.class)
	public ResponseEntity<String> handleScheduleNotFoundException(ScheduleNotFoundException e) {
		log.error("excpetion = {}, message = {}", e.getClass(), e.getMessage());
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	*/
}
