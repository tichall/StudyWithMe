package december.spring.studywithme.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String contents;
}