package december.spring.studywithme.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String refreshToken;


	public RefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
