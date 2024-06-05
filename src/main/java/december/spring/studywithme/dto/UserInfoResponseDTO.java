package december.spring.studywithme.dto;

import december.spring.studywithme.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UserInfoResponseDTO {


    private Long id;

    private String userId;

    private String email;

    private String introduce;

    public UserInfoResponseDTO(User user) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.introduce = introduce;
    }
}
