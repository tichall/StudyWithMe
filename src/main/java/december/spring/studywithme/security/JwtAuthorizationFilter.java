package december.spring.studywithme.security;

import december.spring.studywithme.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // 토큰 검증
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getJwtFromHeader(req);

        if (StringUtils.hasText(accessToken)) {
            try {
                if (jwtUtil.validateToken(accessToken)) {
                    String username = jwtUtil.getUsernameFromToken(accessToken);
                    setAuthentication(username);
                }
            } catch (ExpiredJwtException e) {
                handleExpiredAccessToken(req, res, e);
            } catch (JwtException | IllegalArgumentException e) {
                handleInvalidAccessToken(res);
                return; // 에러 응답을 보낸 경우 필터 체인 중단
            }
        }

        filterChain.doFilter(req, res);
    }

    //리프레시 토큰 검증
    private void handleExpiredAccessToken(HttpServletRequest req, HttpServletResponse res, ExpiredJwtException e) throws IOException {
        String refreshToken = jwtUtil.getJwtRefreshTokenFromHeader(req);

        if (StringUtils.hasText(refreshToken) && jwtUtil.validateRefreshToken(refreshToken)) {
            String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
            String newAccessToken = jwtUtil.createAccessToken(username);

            res.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);
            res.addHeader(JwtUtil.REFRESH_HEADER, refreshToken);

            setAuthentication(username);

            log.info("토큰 생성 완료!");
        } else {
            sendErrorResponse(res, "유효하지 않은 리프레시 토큰입니다.");
        }
    }

    //유효하지 않은 액세스 토큰이 들어올 경우
    private void handleInvalidAccessToken(HttpServletResponse res) throws IOException {
        sendErrorResponse(res, "유효하지 않은 액세스 토큰입니다.");
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 에러 메시지 응답
    private void sendErrorResponse(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = res.getWriter();
        writer.write("{\"message\":\"" + message + "\"}");
        writer.flush();
    }
}
