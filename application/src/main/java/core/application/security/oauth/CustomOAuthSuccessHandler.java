package core.application.security.oauth;

import core.application.security.model.TokenCategory;
import core.application.security.token.JwtTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * OAuth 인증 성공 시 호출되는 핸들러
 */
@Component
public class CustomOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenUtil jwtUtil;

    /**
     * CustomSuccessHandler 생성자
     * @param jwtUtil JWT 관련 유틸리티
     */
    public CustomOAuthSuccessHandler(JwtTokenUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 인증 성공 후 처리 메서드
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authentication 인증 정보 객체
     * @throws IOException, ServletException 예외 발생 시 처리
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 사용자 정보를 OAuth2 사용자 객체로 변환
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 인증된 사용자의 권한 정보를 가져옴
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // Access Token 쿠키로 반환
        String accessToken = jwtUtil.creatAccessToken(customOAuth2User.getUserEmail(), customOAuth2User.getUserId(), role, TokenCategory.OAuth.toString());
        String refreshToken = jwtUtil.creatRefreshToken(customOAuth2User.getUserEmail(), TokenCategory.OAuth.toString());
        response.addCookie(createCookie("accessToken", accessToken));
        response.addCookie(createCookie("refreshToken", refreshToken));

        // 인증 성공 후 리다이렉트할 URL (프론트 측 url로 수정 필요)
        response.sendRedirect("http://localhost:8080/profile");
    }

    /**
     * 쿠키 생성 메서드
     *
     * @param key 쿠키 이름
     * @param value 쿠키 값
     * @return 생성된 HTTP 쿠키 객체
     */
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(3600000);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
