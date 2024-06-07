package december.spring.studywithme.dto;

import december.spring.studywithme.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponseDTO {


    private Long id;

    private String userId;

    private String email;

    private String introduce;

    public UserProfileResponseDTO(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.introduce = user.getIntroduce();
    }
}