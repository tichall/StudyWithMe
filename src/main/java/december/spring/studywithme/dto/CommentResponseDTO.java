package december.spring.studywithme.dto;

import december.spring.studywithme.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDTO {
    private String userId;
    private Long postId;
    private String contents;
    private LocalDateTime createAt;
    private LocalDateTime modifyAt;

    public CommentResponseDTO(Comment comment) {
        this.userId = comment.getUser().getUserId();
        this.postId = comment.getPost().getId();
        this.contents = comment.getContents();
        this.createAt = comment.getCreateAt();
        this.modifyAt = comment.getModifyAt();
    }
}
