package december.spring.studywithme.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "likes")
public class Like extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private boolean isLike;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentsType contentsType;

    @Builder
    public Like(User user, Long targetId, ContentsType contentsType, boolean isLike) {
        this.user = user;
        this.targetId = targetId;
        this.contentsType = contentsType;
        this.isLike = isLike;
    }

    public void update(boolean isLike) {
        this.isLike = isLike;
    }
}
