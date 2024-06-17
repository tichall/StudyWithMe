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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    final Integer PAGE_SIZE = 10;
    Integer TOTAL_PAGES;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostService postService;

    @Mock
    PostRequestDTO requestDTO;

    User createUser() {
        return User.builder()
                .userId("testUser1234")
                .password("testUser1234!!")
                .name("테스트 유저")
                .email("testUser@gmail.com")
                .statusChangedAt(LocalDateTime.now())
                .build();
    }
    Post createPost() {
        return Post.builder()
                .user(createUser())
                .title("테스트 제목")
                .contents("테스트 내용")
                .build();
    }

    List<Post> createPostList() {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            Post post = Post.builder()
                    .user(createUser())
                    .title("테스트 제목 " + i)
                    .contents("테스트 내용 " + i)
                    .build();
            postList.add(post);
        }
        int pages = postList.size() / 10;
        TOTAL_PAGES = postList.size() % 10 == 0 ? pages : pages + 1;
        return postList;
    }


    @Test
    @DisplayName("게시글 생성 성공")
    void 게시글_생성_성공() {
        // given
        User user = createUser();

        String title = "테스트 제목";
        String contents = "테스트 내용";

        given(requestDTO.getTitle()).willReturn(title);
        given(requestDTO.getContents()).willReturn(contents);

        Post post = Post.builder()
                .user(user)
                .title(title)
                .contents(contents)
                .build();

        given(postRepository.save(Mockito.any(Post.class))).willReturn(post);

        // when
        PostResponseDTO responseDTO = postService.createPost(user, requestDTO);

        // then
        assertEquals(user.getUserId(), responseDTO.getUserId());
        assertEquals(title, responseDTO.getTitle());
        assertEquals(contents, responseDTO.getContents());
    }

    @Test
    @DisplayName("게시글 단일 조회 성공")
    void 게시글_단일_조회_성공() {
        // given
        Long id = 1L;
        Post post = createPost();
        given(postRepository.findById(id)).willReturn(Optional.of(post));

        // when
        PostResponseDTO responseDTO = postService.getPost(id);

        // then
        assertEquals(post.getUser().getUserId(), responseDTO.getUserId());
        assertEquals(post.getTitle(), responseDTO.getTitle());
        assertEquals(post.getContents(), responseDTO.getContents());
    }

    @Test
    @DisplayName("게시글 전체 페이지 조회 성공")
    void 게시글_전체_페이지_조회_성공() {
        // given
        Integer page = 1;
        String sortBy = "createdAt";
        String from = "2024-06-16";
        String to = "2024-06-17";
        List<Post> postList = createPostList();
        Pageable pageable = postService.createPageable(page, sortBy);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        given(postRepository.findPostPageByPeriod(
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)
                )
        ).willReturn(postPage);

        // when
        PostPageResponseDTO responseDTO = postService.getPostPage(page, sortBy, from, to);

        // then
        assertEquals(page, responseDTO.getCurrentPage());
        assertEquals(postList.size(), responseDTO.getTotalElements());
        assertEquals(TOTAL_PAGES, responseDTO.getTotalPages());
        assertEquals(PAGE_SIZE, responseDTO.getSize());
        assertEquals(sortBy + ": DESC", responseDTO.getSortBy());
    }

    @Test
    @DisplayName("게시글 전체 페이지 조회 성공 - 시작일자만 입력")
    void 게시글_전체_페이지_조회_성공1() {
        // given
        Integer page = 1;
        String sortBy = "createdAt";
        String from = "2024-06-16";
        String to = null;
        List<Post> postList = createPostList();
        Pageable pageable = postService.createPageable(page, sortBy);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        given(postRepository.findPostPageByStartDate(
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)
                )
        ).willReturn(postPage);

        // when
        PostPageResponseDTO responseDTO = postService.getPostPage(page, sortBy, from, to);

        // then
        verify(postRepository, times(1)).findPostPageByStartDate(Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class));
        assertEquals(page, responseDTO.getCurrentPage());
        assertEquals(postList.size(), responseDTO.getTotalElements());
        assertEquals(TOTAL_PAGES, responseDTO.getTotalPages());
        assertEquals(PAGE_SIZE, responseDTO.getSize());
        assertEquals(from, responseDTO.getFrom());
        assertNull(responseDTO.getTo());
        assertEquals(sortBy + ": DESC", responseDTO.getSortBy());
    }

    @Test
    @DisplayName("게시글 전체 페이지 조회 성공 - 마지막 일자만 입력")
    void 게시글_전체_페이지_조회_성공2() {
        // given
        Integer page = 1;
        String sortBy = "createdAt";
        String from = null;
        String to = "2024-06-17";
        List<Post> postList = createPostList();
        Pageable pageable = postService.createPageable(page, sortBy);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        given(postRepository.findPostPageByFinishDate(
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)
                )
        ).willReturn(postPage);

        // when
        PostPageResponseDTO responseDTO = postService.getPostPage(page, sortBy, from, to);

        // then
        verify(postRepository, times(1)).findPostPageByFinishDate(Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class));
        assertEquals(page, responseDTO.getCurrentPage());
        assertEquals(postList.size(), responseDTO.getTotalElements());
        assertEquals(TOTAL_PAGES, responseDTO.getTotalPages());
        assertEquals(PAGE_SIZE, responseDTO.getSize());
        assertNull(responseDTO.getFrom());
        assertEquals(to, responseDTO.getTo());
        assertEquals(sortBy + ": DESC", responseDTO.getSortBy());
    }


    @Test
    @DisplayName("게시글 전체 페이지 조회 성공 - 기간 입력 X")
    void 게시글_전체_페이지_조회_성공3() {
        // given
        Integer page = 1;
        String sortBy = "createdAt";
        String from = null;
        String to = null;
        List<Post> postList = createPostList();
        Pageable pageable = postService.createPageable(page, sortBy);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        given(postRepository.findAll(
                        Mockito.any(Pageable.class)
                )
        ).willReturn(postPage);

        // when
        PostPageResponseDTO responseDTO = postService.getPostPage(page, sortBy, from, to);

        // then
        verify(postRepository, times(1)).findAll(
                Mockito.any(Pageable.class));
        assertEquals(page, responseDTO.getCurrentPage());
        assertEquals(postList.size(), responseDTO.getTotalElements());
        assertEquals(TOTAL_PAGES, responseDTO.getTotalPages());
        assertEquals(PAGE_SIZE, responseDTO.getSize());
        assertNull(responseDTO.getFrom());
        assertNull(responseDTO.getTo());
        assertEquals(sortBy + ": DESC", responseDTO.getSortBy());
    }

    @Test
    @DisplayName("게시글 전체 페이지 조회 실패 - 날짜 형식 오류")
    void 게시글_전체_페이지_조회_실패() {
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
    @DisplayName("게시글 전체 페이지 조회 실패 - 올바르지 않은 기간")
    void 게시글_전체_페이지_조회_실패2() {
        // given
        Integer page = 1;
        String sortBy = "createdAt";
        String from = "2024-06-16";
        String to = "2024-06-15";

        // when - then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> postService.getPostPage(page, sortBy, from, to));

        assertEquals(e.getMessage(), "기간 설정이 올바르지 않습니다.");
    }

    @Test
    @DisplayName("게시글 전체 페이지 조회 실패 - 게시글이 0개일 때")
    void 게시글_전체_페이지_조회_실패3() {
        // given
        Integer page = 1;
        String sortBy = "createdAt";
        String from = "2024-06-16";
        String to = "2024-06-17";
        List<Post> postList = new ArrayList<>();
        Pageable pageable = postService.createPageable(page, sortBy);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        given(postRepository.findPostPageByPeriod(
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)
                )
        ).willReturn(postPage);

        // when - then
        NoContentException e = assertThrows(NoContentException.class, () -> postService.getPostPage(page, sortBy, from, to));

        assertEquals(e.getMessage(), "게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 전체 페이지 조회 실패 - 존재하지 않는 페이지 접근")
    void 게시글_전체_페이지_조회_실패4() {
        // given
        Integer page = 5;
        String sortBy = "createdAt";
        String from = "2024-06-16";
        String to = "2024-06-17";
        List<Post> postList = createPostList(); // 30개
        Pageable pageable = postService.createPageable(page, sortBy);
        Page<Post> postPage = new PageImpl<>(postList);
        System.out.println(postPage.getTotalPages()); // 3 -> 7?

        given(postRepository.findPostPageByPeriod(
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)
                )
        ).willReturn(postPage);

        // when - then
        PageException e = assertThrows(PageException.class, () -> postService.getPostPage(page, sortBy, from, to));

        assertEquals(e.getMessage(), "페이지가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 수정 성공")
    @Transactional
    void 게시글_수정_성공() {
        // given
        Long id = 1L;
        User user = createUser();
        Post post = createPost();

        String title = "제목 수정";
        String contents = "내용 수정";
        given(requestDTO.getTitle()).willReturn(title);
        given(requestDTO.getContents()).willReturn(contents);
        given(postRepository.findById(id)).willReturn(Optional.of(post));

        // when
        PostResponseDTO responseDTO = postService.updatePost(id, user, requestDTO);

        // then
        assertEquals(title, responseDTO.getTitle());
        assertEquals(contents, responseDTO.getContents());
    }

    @Test
    @DisplayName("게시글 수정 실패 - 작성자 권한 없음")
    void 게시글_수정_실패1() {
        // given
        Long id = 1L;
        User invalidUser = User.builder()
                .userId("invalidUser123")
                .build();

        Post post = createPost();

        given(postRepository.findById(id)).willReturn(Optional.of(post));

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.updatePost(id, invalidUser, requestDTO));

        assertEquals(e.getMessage(),"작성자가 아니므로, 접근이 제한됩니다.");
    }

    @Test
    @DisplayName("게시글 수정 실패 - 존재하지 않는 게시글")
    void 게시글_수정_실패2() {
        // given
        Long id = 1L;
        User user = createUser();
        Post post = createPost();
        given(postRepository.findById(id)).willReturn(Optional.empty());

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.updatePost(id, user, requestDTO));

        assertEquals(e.getMessage(), "게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void 게시글_삭제_성공() {
        // given
        Long id = 1L;
        User user = createUser();
        Post post = createPost();

        given(postRepository.findById(id)).willReturn(Optional.of(post));
        doNothing().when(postRepository).delete(post);

        // when
        postService.deletePost(id, user);

        // then
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 작성자 권한 없음")
    void 게시글_삭제_실패1() {
        // given
        Long id = 1L;
        User invalidUser = User.builder()
                .userId("invalidUser123")
                .build();

        Post post = createPost();

        given(postRepository.findById(id)).willReturn(Optional.of(post));

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.deletePost(id, invalidUser));

        assertEquals(e.getMessage(),"작성자가 아니므로, 접근이 제한됩니다.");
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시글")
    void 게시글_삭제_실패2() {
        // given
        Long id = 1L;
        User user = createUser();
        given(postRepository.findById(id)).willReturn(Optional.empty());

        // when - then
        PostException e = assertThrows(PostException.class, () -> postService.deletePost(id, user));

        assertEquals(e.getMessage(), "게시글이 존재하지 않습니다.");
    }
}
