package december.spring.studywithme.dto;

import java.time.LocalDateTime;

import december.spring.studywithme.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
	private Long id;
	private String userId;
	private String name;
	private String email;
	private LocalDateTime createAt;
	private LocalDateTime modifyAt;
	
	public UserResponseDTO(User user) {
		this.id = user.getId();
		this.userId = user.getUserId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.createAt = user.getCreateAt();
		this.modifyAt = user.getModifyAt();
	}
	
}
