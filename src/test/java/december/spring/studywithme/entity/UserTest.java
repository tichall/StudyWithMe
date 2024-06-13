package december.spring.studywithme.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class UserTest {
    // given
    User user = User.builder()
            .userId("testUser123")
            .name("Jenny")
            .password("testUser123~!!")
            .email("hello12345@gmail.com")
            .introduce("테스트 유저입니다.")
            .userType(UserType.UNVERIFIED)
            .statusChangedAt(LocalDateTime.now())
            .build();

    @Test
    void 회원상태변경_활성화() {
        // when
        user.ActiveUser();

        // then
        assertEquals(UserType.ACTIVE, user.getUserType());

    }

    @Test
    void 회원상태변경_탈퇴() {
        // when
        user.withdrawUser();

        // then
        assertEquals(UserType.DEACTIVATED, user.getUserType());
    }

    @Test
    void RefreshToken_초기화() {
        // given
        String refreshToken = "akdfja0sud2jk3asdmfkasid0ianspkd";

        // when
        user.refreshTokenReset(refreshToken);

        // then
        assertEquals(refreshToken, user.getRefreshToken());
    }

    @Test
    void 프로필_수정() {
        // given
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
        String newPassword = "newPassword~!!";

        // when
        user.changePassword(newPassword);

        // then
        assertEquals(newPassword, user.getPassword());
    }
}
