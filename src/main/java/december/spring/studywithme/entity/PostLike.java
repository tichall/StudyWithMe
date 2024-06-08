package december.spring.studywithme.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Getter
@Entity
@NoArgsConstructor
public class PostLike extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private boolean isLike;

    @Builder
    public PostLike(User user, Post post, boolean isLike) {
        this.user = user;
        this.post = post;
        this.isLike = isLike;
    }

    public void update(boolean isLike) {
        this.isLike = isLike;
    }

}
