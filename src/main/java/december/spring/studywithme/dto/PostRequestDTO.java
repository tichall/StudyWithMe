package december.spring.studywithme.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String contents;
}