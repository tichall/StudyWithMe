package december.spring.studywithme.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import december.spring.studywithme.dto.LoginRequestDTO;
import december.spring.studywithme.dto.ResponseMessage;
import december.spring.studywithme.entity.User;
import december.spring.studywithme.jwt.JwtUtil;
import december.spring.studywithme.repository.UserRepository;
import december.spring.studywithme.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;


    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDTO requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUserId(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();

        String accessToken = jwtUtil.createAccessToken(username);
        String refreshToken = jwtUtil.createRefreshToken(username);

        User user = ((UserDetailsImpl) authResult.getPrincipal()).getUser();
        user.refreshTokenReset(refreshToken);
        userRepository.save(user);

        // 응답 헤더에 토큰 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(JwtUtil.REFRESH_HEADER, refreshToken);

        // JSON 응답 작성
        writeJsonResponse(response, HttpStatus.OK, "로그인에 성공했습니다.", authResult.getName());

        log.info("User = {}, message = {}", username, "로그인에 성공했습니다.");
    }

    private void writeJsonResponse(HttpServletResponse response, HttpStatus status, String message, String data) throws IOException {
        ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();

        String jsonResponse = new ObjectMapper().writeValueAsString(responseMessage);
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(400);
        response.setContentType("application/json; charset=UTF-8");
        try {
            response.getWriter().write("{\"message\":\"회원을 찾을 수 없습니다.\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
