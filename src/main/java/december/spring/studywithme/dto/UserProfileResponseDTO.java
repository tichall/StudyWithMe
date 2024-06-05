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
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.introduce = introduce;
    }
}