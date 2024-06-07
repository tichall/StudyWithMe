package december.spring.studywithme.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CertificationNumberRequestDTO {
	@NotBlank
	private String code;
}
