package december.spring.studywithme.dto;

import december.spring.studywithme.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDTO {
    private String userId;
    private String title;
    private String contents;
    private Long likes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostResponseDTO(Post post) {
        this.userId = post.getUser().getUserId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.likes = post.getLikes();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }
}
