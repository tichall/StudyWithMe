package december.spring.studywithme.dto;

import december.spring.studywithme.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDTO {
    private String userId;
    private String title;
    private String contents;
    private LocalDateTime createAt;
    private LocalDateTime modifyAt;

    public PostResponseDTO(Post post) {
        this.userId = post.getUser().getUserId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.createAt = post.getCreateAt();
        this.modifyAt = post.getModifyAt();
    }
}
