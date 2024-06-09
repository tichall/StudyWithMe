package december.spring.studywithme.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "user")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String userId;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    @NotNull
    @Column(nullable = false, unique = true)
    private String email;
    
    private String introduce;
    
    
    @Enumerated(value = EnumType.STRING)
    private UserType userType;
    
    @Column
    private String refreshToken;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime statusChangedAt;
    
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Post> postList;
    
    @Builder
    public User(String userId, String password, String name, String email, String introduce, UserType userType, LocalDateTime statusChangedAt) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.introduce = introduce;
        this.userType = userType;
        this.statusChangedAt = statusChangedAt;
    }
    
    //회원 상태 변경 - 탈퇴 회원
    public void withdrawUser() {
        this.userType = UserType.DEACTIVATED;
    }
    
    //회원 상태 변경 - 인증 회원
    public void ActiveUser() {
        this.userType = UserType.ACTIVE;
    }
    
    //로그인시 리프레시 토큰 초기화
    @Transactional
    public void refreshTokenReset(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    //프로필 수정
    public void editProfile(String name, String introduce) {
        this.name = name;
        this.introduce = introduce;
    }
    
    //비밀번호 변경
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}
