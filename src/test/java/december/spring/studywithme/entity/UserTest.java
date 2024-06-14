package december.spring.studywithme.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class UserTest {
    private User createUser() {
        User user = User.builder()
                .userId("testUser123")
                .name("Jenny")
                .password("testUser123~!!")
                .email("hello12345@gmail.com")
                .introduce("테스트 유저입니다.")
                .userType(UserType.UNVERIFIED)
                .statusChangedAt(LocalDateTime.now())
                .build();
        return user;
    }

    @Test
    void user_생성() {
        // given
        String userId = "testNewUser12345";
        String password = "testNewUser~!!";
        String name = "서연";
        String email = "0011@gmail.com";
        String introduce = "안녕하세요!";
        UserType userType = UserType.ACTIVE;
        LocalDateTime statusChangedAt = LocalDateTime.now();

        // when
        User user = User.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .email(email)
                .introduce(introduce)
                .userType(userType)
                .statusChangedAt(LocalDateTime.now())
                .build();

        // then
        assertEquals(user.getUserId(), userId);
        assertEquals(user.getPassword(), password);
        assertEquals(user.getName(), name);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getIntroduce(), introduce);
        assertEquals(user.getUserType(), userType);
        assertEquals(user.getStatusChangedAt(), statusChangedAt);

    }

    @Test
    void 회원상태변경_활성화() {
        // given
        User user = createUser();

        // when
        user.ActiveUser();

        // then
        assertEquals(UserType.ACTIVE, user.getUserType());

    }

    @Test
    void 회원상태변경_탈퇴() {
        // given
        User user = createUser();

        // when
        user.withdrawUser();

        // then
        assertEquals(UserType.DEACTIVATED, user.getUserType());
    }

    @Test
    void RefreshToken_초기화() {
        // given
        User user = createUser();
        String refreshToken = "akdfja0sud2jk3asdmfkasid0ianspkd";

        // when
        user.refreshTokenReset(refreshToken);

        // then
        assertEquals(refreshToken, user.getRefreshToken());
    }

    @Test
    void 프로필_수정() {
        // given
        User user = createUser();
        String editName = "editName";
        String editIntroduce = "editIntroduce";

        // when
        user.editProfile(editName, editIntroduce);

        // then
        assertEquals(editName, user.getName());
        assertEquals(editIntroduce, user.getIntroduce());
    }

    @Test
    void 비밀번호_변경() {
        // given
        User user = createUser();
        String newPassword = "newPassword~!!";

        // when
        user.changePassword(newPassword);

        // then
        assertEquals(newPassword, user.getPassword());
    }
}
