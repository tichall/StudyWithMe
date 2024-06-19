package december.spring.studywithme.service;

import december.spring.studywithme.dto.PostPageResponseDTO;
import december.spring.studywithme.dto.PostRequestDTO;
import december.spring.studywithme.dto.PostResponseDTO;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.entity.User;
import december.spring.studywithme.exception.NoContentException;
import december.spring.studywithme.exception.PageException;
import december.spring.studywithme.exception.PostException;
import december.spring.studywithme.repository.PostRepository;
import december.spring.studywithme.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostServiceIntegrationTest {
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    // 게시글 작성자
    User user;
    PostResponseDTO responseDTO;

//    @Test
//    void setUser() {
//        user = User.builder()
//                .userId("test2User1234")
//                .password("testUser1234!!")
//                .name("테스트 유저2")
//                .email("testUser2@gmail.com")
//                .statusChangedAt(LocalDateTime.now())
//                .build();
//
//        userRepository.save(user);
//    }

    @Test
    @Order(1)
    @DisplayName("게시글 생성 성공")
    void test1() {
        // given
        user = userRepository.findById(1L).orElse(null);
        String title = "서울 지역 스터디 모집합니다.";
        String contents = "성실히 참여하실 분 5분 구해요~";

        PostRequestDTO requestDTO = mock(PostRequestDTO.class);
        given(requestDTO.getTitle()).willReturn(title);
        given(requestDTO.getContents()).willReturn(contents);

        // when
        responseDTO = postService.createPost(user, requestDTO);

        // then
        assertEquals(title, responseDTO.getTitle());
        assertEquals(contents, responseDTO.getContents());
        assertEquals(user.getUserId(), responseDTO.getUserId());
        assertEquals(0L, responseDTO.getLikes());
        assertNotNull(responseDTO.getCreatedAt());
        assertNotNull(responseDTO.getModifiedAt());
    }

    @Test
    @Order(2)
    @DisplayName("게시글 단일 조회 성공")
    @Transactional
    void test2() {
        // given
        Long id = 1L;
        Post post = postRepository.findById(id).orElse(null);

        // when
        PostResponseDTO postResponseDTO = postService.getPost(id);

        // then
        assertEquals(post.getUser().getUserId(), postResponseDTO.getUserId());
        assertEquals(post.getTitle(), postResponseDTO.getTitle());
        assertEquals(post.getContents(), postResponseDTO.getContents());
    }

    @Test
    @Order(3)
    @DisplayName("게시글 단일 조회 실패")
    void test3() {
        // given
        Long id = 0L;

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.getPost(id));
        assertEquals(e.getMessage(), "게시글이 존재하지 않습니다.");
    }

    @Test
    @Order(4)
    @DisplayName("게시글 전체 페이지 조회")
    @Transactional
    void test4() {
        // given
        Integer page = 1;
        String sortBy = "createdAt";
        String from = "2024-06-16";
        String to = "2024-06-17";

        // when
        PostPageResponseDTO postPageResponseDTO = postService.getPostPage(page,sortBy, from, to);

        // then
        assertEquals(page, postPageResponseDTO.getCurrentPage());
        assertEquals(sortBy + ": DESC", postPageResponseDTO.getSortBy());
    }

    @Test
    @Order(5)
    @DisplayName("게시글 전체 페이지 조회 실패 - 날짜 형식 오류")
    void test5() {
        // given
        Integer page = 1;
        String sortBy = "createdAt";
        String from = "20240616";
        String to = "20240617";

        // when - then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> postService.getPostPage(page, sortBy, from, to));

        assertEquals(e.getMessage(), "날짜 형식이 올바르지 않습니다. yyyy-mm-dd 형식으로 입력해주세요!");
    }

    @Test
    @Order(6)
    @DisplayName("게시글 전체 페이지 조회 실패 - 페이지 지정 오류")
    void test6() {
        // given
        Integer page = 0;
        String sortBy = "createdAt";
        String from = "2024-06-16";
        String to = "2024-06-17";

        // when - then
        PageException e = assertThrows(PageException.class, () -> postService.getPostPage(page, sortBy, from, to));

        assertEquals(e.getMessage(), "페이지는 1부터 존재합니다.");
    }

    @Test
    @Order(7)
    @DisplayName("게시글 전체 페이지 조회 실패 - 존재하지 않는 페이지")
    void test7() {
        // given
        Integer page = 1000;
        String sortBy = "likes";
        String from = "2024-06-16";
        String to = "2024-06-17";

        // when - then
        PageException e = assertThrows(PageException.class, () -> postService.getPostPage(page, sortBy, from, to));

        assertEquals(e.getMessage(), "페이지가 존재하지 않습니다.");

    }

    @Test
    @Order(8)
    @DisplayName("게시글 수정 성공")
    @Transactional
    void test8() {
        // given
        Long id = 1L;
        user = userRepository.findById(1L).orElse(null);
        String title = "제목 수정";
        String contents = "내용 수정";
        PostRequestDTO requestDTO = mock(PostRequestDTO.class);
        given(requestDTO.getTitle()).willReturn(title);
        given(requestDTO.getContents()).willReturn(contents);

        // when
        PostResponseDTO postResponseDTO = postService.updatePost(id, user, requestDTO);

        // then
        assertEquals(id, postResponseDTO.getId());
        assertEquals(user.getUserId(), postResponseDTO.getUserId());
        assertEquals(title, postResponseDTO.getTitle());
        assertEquals(contents, postResponseDTO.getContents());
    }

    @Test
    @Order(9)
    @DisplayName("게시글 수정 실패 - 작성자 권한 없음")
    @Transactional
    void test9() {
        // given
        Long id = 1L;
        user = userRepository.findById(2L).orElse(null);
        String title = "제목 수정";
        String contents = "내용 수정";
        PostRequestDTO requestDTO = mock(PostRequestDTO.class);
        given(requestDTO.getTitle()).willReturn(title);
        given(requestDTO.getContents()).willReturn(contents);

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.updatePost(id, user, requestDTO));
        assertEquals(e.getMessage(), "작성자가 아니므로, 접근이 제한됩니다.");
    }

    @Test
    @Order(10)
    @DisplayName("게시글 수정 실패 - 존재하지 않는 게시글")
    @Transactional
    void test10() {
        // given
        Long id = 1000L;
        user = userRepository.findById(1L).orElse(null);
        String title = "제목 수정";
        String contents = "내용 수정";
        PostRequestDTO requestDTO = mock(PostRequestDTO.class);
        given(requestDTO.getTitle()).willReturn(title);
        given(requestDTO.getContents()).willReturn(contents);

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.updatePost(id, user, requestDTO));
        assertEquals(e.getMessage(), "게시글이 존재하지 않습니다.");
    }

    @Test
    @Order(11)
    @DisplayName("게시글 삭제 성공")
    @Transactional
    void test11() {
        // given
        Long id = 1L;
        user = userRepository.findById(1L).orElse(null);

        // when
        postService.deletePost(id, user);

        // then
        assertTrue(postRepository.findById(id).isEmpty());
    }

    @Test
    @Order(12)
    @DisplayName("게시글 삭제 실패 - 작성자 권한 없음")
    void test12() {
        // given
        Long id = 1L;
        user = userRepository.findById(2L).orElse(null);

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.deletePost(id, user));
        assertEquals(e.getMessage(), "작성자가 아니므로, 접근이 제한됩니다.");
    }

    @Test
    @Order(13)
    @DisplayName("게시글 수정 실패 - 존재하지 않는 게시글")
    @Transactional
    void test13() {
        // given
        Long id = 1000L;
        user = userRepository.findById(1L).orElse(null);

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.deletePost(id, user));
        assertEquals(e.getMessage(), "게시글이 존재하지 않습니다.");
    }
}
