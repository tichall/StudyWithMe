package december.spring.studywithme.entity;

import december.spring.studywithme.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "post")
public class Post extends Timestamped{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String contents;

	@OneToMany(mappedBy = "post", orphanRemoval = true)
	private List<Comment> commentList;
	
	@Builder
	public Post(User user, String title, String contents)  {
		this.user = user;
		this.title = title;
		this.contents = contents;
	}

	public void update(PostRequestDto requestDto) {
		this.title = requestDto.getTitle();
		this.contents = requestDto.getContents();
	}
}
