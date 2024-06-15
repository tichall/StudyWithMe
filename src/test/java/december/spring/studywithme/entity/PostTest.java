package december.spring.studywithme.entity;

import static org.junit.jupiter.api.Assertions.*;

import december.spring.studywithme.dto.PostRequestDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class PostTest {
    User user = User.builder()
            .userId("helloTempUser")
            .password("testpassword~!!")
            .name("서연")
            .email("0011@gmail.com")
            .userType(UserType.ACTIVE)
            .statusChangedAt(LocalDateTime.now())
            .build();

    private Post createPost() {
        return Post.builder()
                .user(user)
                .title("제목")
                .contents("내용")
                .build();
    }

    @Test
    void 게시물_생성_테스트() {
        // given
        String title = "공부할 사람 구해요";
        String contents = "내일부터 같이 열심히 해봐요!";

        // when
        Post post = Post.builder()
                .user(user)
                .title(title)
                .contents(contents)
                .build();

        // then
        assertEquals(post.getUser(), user);
        assertEquals(post.getTitle(), title);
        assertEquals(post.getContents(), contents);
        assertEquals(post.getLikes(), 0L);
    }

    @Test
    void 게시글_수정_테스트() {
        // given
        Post post = createPost();
        PostRequestDTO requestDTO = new PostRequestDTO(
                "제목 (수정)",
                "내용 (수정)"
        );

        // when
        post.update(requestDTO);

        // then
        assertEquals(post.getTitle(), requestDTO.getTitle());
        assertEquals(post.getContents(), requestDTO.getContents());
    }

    @Test
    void 게시글_좋아요_업데이트_테스트() {
        // given
        Post post = createPost();
        Long likes = 20L;

        // when
        post.updatePostLikes(likes);

        // then
        assertEquals(post.getLikes(), likes);
    }
}
