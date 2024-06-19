package december.spring.studywithme.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import december.spring.studywithme.config.SecurityConfig;
import december.spring.studywithme.controller.PostController;
import december.spring.studywithme.controller.UserController;
import december.spring.studywithme.dto.PostPageResponseDTO;
import december.spring.studywithme.dto.PostRequestDTO;
import december.spring.studywithme.dto.PostResponseDTO;
import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.entity.User;
import december.spring.studywithme.entity.UserType;
import december.spring.studywithme.security.MockSpringSecurityFilter;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {PostController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
public class PostControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private List<Post> setData() {
        User user = User.builder()
                .userId("helloTempUser")
                .password("testpassword~!!")
                .name("서연")
                .email("0011@gmail.com")
                .userType(UserType.ACTIVE)
                .statusChangedAt(LocalDateTime.now())
                .build();

        List<Post> postList = new ArrayList<>();
        Post post1 = Post.builder()
                .user(user)
                .title("공부할 사람")
                .contents("당장 공부할 사람 모여라")
                .build();
        post1.setId(1L);

        Post post2 = Post.builder()
                .user(user)
                .title("공부할 사람 2")
                .contents("당장 공부할 사람 모여라 2")
                .build();
        post2.setId(2L);

        Post post3 = Post.builder()
                .user(user)
                .title("공부할 사람 3")
                .contents("당장 공부할 사람 모여라 3")
                .build();
        post3.setId(3L);

        postList.add(post1);
        postList.add(post2);
        postList.add(post3);

        return postList;
    }

    private User mockUserSetup() {
        String userId = "testUserHello123";
        String name = "test 유저";
        String password = "testUserHello~!!";
        String email = "tesUser@gmail.com";
        String introduce = "안녕하세요!";
        UserType type = UserType.ACTIVE;
        LocalDateTime statusChangedAt = LocalDateTime.now();

        User testUser = User.builder()
                .userId(userId)
                .name(name)
                .password(password)
                .email(email)
                .introduce(introduce)
                .userType(type)
                .statusChangedAt(statusChangedAt)
                .build();

        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        // 인증이 완료된 객체 만들기?
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());

        return testUser;
    }

    @Test
    void 게시글_등록() throws Exception {
        // given
        User testUser = this.mockUserSetup();
        String title = "공부할 사람 구합니다.";
        String content = "이번 주부터 같이 열심히 공부합시다~";
        PostRequestDTO requestDTO = new PostRequestDTO(
                title,
                content
        );

        String body = objectMapper.writeValueAsString(requestDTO);

        Post post = new Post(testUser, title, content);
        PostResponseDTO responseDTO = new PostResponseDTO(post);

        // when
        Mockito.when(postService.createPost(Mockito.any(User.class), Mockito.any(PostRequestDTO.class))).thenReturn(responseDTO);

        mvc.perform(post("/api/posts")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("게시글 생성이 완료되었습니다."))
                .andExpect(jsonPath("$.data.userId").value(responseDTO.getUserId()))
                .andExpect(jsonPath("$.data.title").value(responseDTO.getTitle()))
                .andExpect(jsonPath("$.data.contents").value(responseDTO.getContents()))
                .andDo(print());
    }

    @Test
    void 게시글_단일_조회() throws Exception {
        // given
        List<Post> postList = setData();
        Post post = postList.get(0);
        PostResponseDTO responseDTO = new PostResponseDTO(post);

        // when - then
        Mockito.when(postService.getPost(Mockito.any(Long.class))).thenReturn(responseDTO);

        mvc.perform(get("/api/posts/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.userId").value(post.getUser().getUserId()))
                .andExpect(jsonPath("$.data.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.contents").value(post.getContents()))
                .andDo(print());
    }

    @Test
    void 전체_게시글_페이지_조회() throws Exception{
        // given
        String from = "2024-05-10";
        String to = "2024-05-12";

        // when - then
        mvc.perform(get("/api/posts?from={from}&to={to}", from, to))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 페이지 조회가 완료되었습니다."))
                .andDo(print());
    }

   @Test
   void 게시글_수정_테스트() throws Exception{
       // given
       User testUser = mockUserSetup();
       String title = "수정 게시글";
       String contents = "내용 수정";

       PostRequestDTO postRequestDTO = new PostRequestDTO(
               title,
               contents
       );

       Post post = Post.builder()
               .user(testUser)
               .title(title)
               .contents(contents)
               .build();

       String body = objectMapper.writeValueAsString(postRequestDTO);

       PostResponseDTO responseDTO = new PostResponseDTO(post);

       Mockito.when(postService.updatePost(Mockito.any(Long.class), Mockito.any(User.class), Mockito.any(PostRequestDTO.class))).thenReturn(responseDTO);

       // when - then
       mvc.perform(put("/api/posts/{id}", 1L)
               .content(body)
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .principal(mockPrincipal)
       )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("게시글 수정이 완료되었습니다."))
               .andExpect(jsonPath("$.data.userId").value(testUser.getUserId()))
               .andExpect(jsonPath("$.data.title").value(post.getTitle()))
               .andExpect(jsonPath("$.data.contents").value(post.getContents()))
               .andDo(print());
   }

   @Test
   void 게시글_삭제_테스트() throws Exception {
       // given
       mockUserSetup();

       // when - then
       mvc.perform(delete("/api/posts/{id}", 1L)
               .principal(mockPrincipal)
       )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("게시글 삭제가 완료되었습니다."))
               .andExpect(jsonPath("$.data").value(1L))
               .andDo(print());

   }
}
