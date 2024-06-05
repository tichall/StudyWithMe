package december.spring.studywithme.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		
		Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
			.collect(Collectors.toMap(
				error -> error.getField(),
				error -> error.getDefaultMessage(),
				(existingValue, newValue) -> existingValue
			));
		
		ObjectMapper mapper = new ObjectMapper();
		String response = "";
		try {
			response = mapper.writeValueAsString(errors);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(UserException.class)
	public ResponseEntity<String> handleUserException(UserException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PostException.class)
	public ResponseEntity<String> handlePostException(PostException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
