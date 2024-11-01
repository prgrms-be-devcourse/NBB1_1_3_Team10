package core.application.security.oauth

import core.application.security.model.TokenCategory
import core.application.security.token.JwtTokenUtil
import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * OAuth 인증 성공 시 호출되는 핸들러
 */
@Component
class CustomOAuthSuccessHandler
/**
 * CustomSuccessHandler 생성자
 * @param jwtUtil JWT 관련 유틸리티
 */(private val jwtUtil: JwtTokenUtil) : SimpleUrlAuthenticationSuccessHandler() {
    /**
     * 인증 성공 후 처리 메서드
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param authentication 인증 정보 객체
     * @throws IOException, ServletException 예외 발생 시 처리
     */
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication
    ) {
        // 사용자 정보를 OAuth2 사용자 객체로 변환
        val customOAuth2User = authentication.principal as CustomOAuth2User

        // 인증된 사용자의 권한 정보를 가져옴
        val authorities = authentication.authorities
        val iterator: Iterator<GrantedAuthority> = authorities.iterator()
        val auth = iterator.next()
        val role = auth.authority

        // Access Token 쿠키로 반환
        val accessToken = jwtUtil.creatAccessToken(
            customOAuth2User.userEmail,
            customOAuth2User.userId,
            role,
            TokenCategory.OAuth.toString()
        )
        val refreshToken = jwtUtil.creatRefreshToken(customOAuth2User.userEmail, TokenCategory.OAuth.toString())
        response.addCookie(createCookie("accessToken", accessToken))
        response.addCookie(createCookie("refreshToken", refreshToken))

        // 인증 성공 후 리다이렉트할 URL (프론트 측 url로 수정 필요)
        response.sendRedirect("http://localhost:8080/profile")
    }

    /**
     * 쿠키 생성 메서드
     *
     * @param key 쿠키 이름
     * @param value 쿠키 값
     * @return 생성된 HTTP 쿠키 객체
     */
    private fun createCookie(key: String, value: String): Cookie {
        val cookie = Cookie(key, value)
        cookie.maxAge = 3600000
        cookie.path = "/"
        cookie.isHttpOnly = true
        return cookie
    }
}
