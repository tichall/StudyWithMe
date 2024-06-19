package december.spring.studywithme.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
public class PostRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setInit() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 생성_성공() {
        // given
        String title = "제목";
        String contents = "내용";

        // when
        PostRequestDTO requestDTO = new PostRequestDTO(
                title,
                contents
        );

        // then
        assertEquals(title, requestDTO.getTitle());
        assertEquals(contents, requestDTO.getContents());
    }

    @Test
    @DisplayName("생성 실패 테스트 : title 공백")
    void 생성_실패() {
        // given
        String title = "";
        String contents = "내용";

        // when
        PostRequestDTO requestDTO = new PostRequestDTO(
                title,
                contents
        );

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(requestDTO);

        // then
        assertThat(violations).anyMatch(v -> v.getMessage().equals("제목은 공백일 수 없습니다."));

    }

    @Test
    @DisplayName("생성 실패 테스트 : contents 공백")
    void 생성_실패_2() {
        // given
        String title = "제목";
        String contents = "";

        // when
        PostRequestDTO requestDTO = new PostRequestDTO(
                title,
                contents
        );

        Set<ConstraintViolation<PostRequestDTO>> violations = validator.validate(requestDTO);

        // then
        assertThat(violations).anyMatch(v -> v.getMessage().equals("내용은 공백일 수 없습니다."));

    }
}
