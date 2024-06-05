package december.spring.studywithme.security;

import december.spring.studywithme.jwt.JwtUtil;
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
import java.util.Optional;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // 토큰 검증
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("filter 시작");
        String tokenValue = jwtUtil.getJwtFromHeader(req);
        System.out.println("tokenValue = "+ tokenValue );
        if (StringUtils.hasText(tokenValue)) {
            if (jwtUtil.validateToken(tokenValue)) {
                // 토큰이 유효한 경우
                AccessToken(tokenValue);
            } else {
                // 토큰이 유효하지 않은 경우
                if (!handleExpiredAccessToken(req, res)) {
                    return; // 에러 응답을 보낸 경우 필터 체인 중단
                }
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인가
    private void AccessToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        try {
            setAuthentication(username);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 리프레시 토큰 검증
    private boolean handleExpiredAccessToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String refreshToken = Optional.ofNullable(req.getHeader(JwtUtil.REFRESH_HEADER))
                .map(header -> header.substring(JwtUtil.BEAR.length()))
                .orElse("");

        if (StringUtils.hasText(refreshToken)) {
            String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
            if (newAccessToken != null) {
                res.setHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);
                AccessToken(newAccessToken.substring(JwtUtil.BEAR.length()));
                log.info("토큰 생성 완료");
                return true;
            } else {
                log.error("Invalid Refresh Token");
                respondWithError(res, "리프레시 토큰이 유효하지 않습니다.");
                return false;
            }
        } else {
            log.error("Token Error!!!");
            respondWithError(res, "리프레시 토큰이 유효하지 않습니다.");
            return false;
        }
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
    private void respondWithError(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write(String.format("{\"message\":\"%s\"}", message));
    }
}
