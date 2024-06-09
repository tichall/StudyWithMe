package december.spring.studywithme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequestDTO {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
