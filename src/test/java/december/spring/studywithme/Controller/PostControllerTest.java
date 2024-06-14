package december.spring.studywithme.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import december.spring.studywithme.config.SecurityConfig;
import december.spring.studywithme.controller.PostController;
import december.spring.studywithme.controller.UserController;
import december.spring.studywithme.dto.PostRequestDTO;
import december.spring.studywithme.dto.PostResponseDTO;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.entity.User;
import december.spring.studywithme.entity.UserType;
import december.spring.studywithme.security.MockSpringSecurityFilter;
import december.spring.studywithme.security.UserDetailsImpl;
import december.spring.studywithme.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDateTime;

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
        Mockito.when(postService.createPost(Mockito.any(UserDetailsImpl.class), Mockito.any(PostRequestDTO.class))).thenReturn(responseDTO);

        MvcResult result = mvc.perform(post("/api/posts")
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
                .andDo(print())
                .andReturn();
    }
}
