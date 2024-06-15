package december.spring.studywithme.entity;

import static org.junit.jupiter.api.Assertions.*;

import december.spring.studywithme.dto.CommentRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;


public class CommentTest {
    private Post post;

    User user = User.builder()
            .userId("commentUser")
            .password("testpassword~!!")
            .name("윤서")
            .email("0899@gmail.com")
            .userType(UserType.ACTIVE)
            .statusChangedAt(LocalDateTime.now())
            .build();

    @BeforeEach
    void createPost() {
        User postUser = User.builder()
                .userId("helloTempUser")
                .password("testpassword~!!")
                .name("서연")
                .email("0011@gmail.com")
                .userType(UserType.ACTIVE)
                .statusChangedAt(LocalDateTime.now())
                .build();

        post = Post.builder()
                .user(postUser)
                .title("제목")
                .contents("내용")
                .build();
    }

    Comment createComment() {
        return Comment.builder()
                .post(post)
                .user(user)
                .contents("응원합니다!")
                .build();
    }

    @Test
    void 댓글_생성() {
        // given
        String contents = "참여하고 싶습니다!";

        // when
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .contents(contents)
                .build();

        // then
        assertEquals(post, comment.getPost());
        assertEquals(user, comment.getUser());
        assertEquals(contents, comment.getContents());
        assertEquals(0L, comment.getLikes());
    }

    @Test
    void 댓글_수정() {
        // given
        Comment comment = createComment();
        String contents = "저 참여하고 싶습니다!";
        CommentRequestDTO requestDTO = mock(CommentRequestDTO.class);
        given(requestDTO.getContents()).willReturn(contents);

        // when
        comment.update(requestDTO);

        // then
        assertEquals(contents, comment.getContents());
    }

    @Test
    void 댓글_좋아요_업데이트() {
        // given
        Comment comment = createComment();
        Long likes = 10L;

        // when
        comment.updateCommentLikes(likes);

        // then
        assertEquals(likes, comment.getLikes());
    }
}
