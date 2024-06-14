package december.spring.studywithme.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class MockSpringSecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {} // FilterConfig를 통해 filter 설정

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 요청에 있는 사용자 정보를 Authentication에 세팅
        SecurityContextHolder.getContext().setAuthentication((Authentication) ((HttpServletRequest) servletRequest).getUserPrincipal());

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // 필터 인스턴스 소멸 전에 실행
        SecurityContextHolder.clearContext();
    }

}
