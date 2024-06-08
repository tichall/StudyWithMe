package december.spring.studywithme.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class CommentLike extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(nullable = false)
    private boolean isLike;


    @Builder
    public CommentLike(User user, Comment comment, boolean isLike) {
        this.user = user;
        this.comment = comment;
        this.isLike = isLike;
    }

    public void update() {
        this.isLike = !this.isLike;
    }

}
