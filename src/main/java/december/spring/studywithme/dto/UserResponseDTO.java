package december.spring.studywithme.dto;

import java.time.LocalDateTime;

import december.spring.studywithme.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
	private Long id;
	private String userId;
	private String name;
	private String email;
	private String introduce;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	
	public UserResponseDTO(User user) {
		this.id = user.getId();
		this.userId = user.getUserId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.introduce = user.getIntroduce();
		this.createdAt = user.getCreatedAt();
		this.modifiedAt = user.getModifiedAt();
	}
	
}
