package december.spring.studywithme.exception;

import java.util.Map;
import java.util.stream.Collectors;

import december.spring.studywithme.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		
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

		ErrorMessage errorMessage = ErrorMessage.builder()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.message(response)
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
	}
	
	@ExceptionHandler({UserException.class, PostException.class, EmailException.class,
			LikeException.class, CommentException.class})
	public ResponseEntity<ErrorMessage> handleNormalException(UserException e) {

		ErrorMessage errorMessage = ErrorMessage.builder()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.message(e.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
	}
	
	@ExceptionHandler(NoContentException.class)
	public ResponseEntity<String> handleNoPostException(NoContentException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
	}
	
}
