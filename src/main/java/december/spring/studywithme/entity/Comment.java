package december.spring.studywithme.entity;

import december.spring.studywithme.dto.CommentRequestDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "comment")
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    private List<CommentLike> commentLikeList;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private Long likes;

    @Builder
    public Comment(Post post, User user, String contents) {
        this.post = post;
        this.user = user;
        this.contents = contents;
        this.likes = 0L;
    }

    public void update(CommentRequestDTO requestDto) {
        this.contents = requestDto.getContents();
    }

    public void updateCommentLikes(Long likes) {
        this.likes = likes;
    }
}

