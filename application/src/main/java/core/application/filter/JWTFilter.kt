package core.application.filter

import core.application.api.exception.CommonForbiddenException
import core.application.security.auth.CustomUserDetails
import core.application.security.model.TokenCategory
import core.application.security.token.TokenService
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * JWT 기반의 인증을 처리하는 필터
 *
 * HTTP 요청에서 Access Token을 추출하여
 * 사용자의 인증 정보를 SecurityContext에 설정
 */
@Slf4j
class JWTFilter
/**
 * JWTFilter 생성자
 *
 * @param tokenService JWT와 관련된 사용자 정보를 처리하는 서비스
 */(var tokenService: TokenService) : OncePerRequestFilter() {
    /**
     * 요청을 필터링하여 Access Token을 검사하고,
     * 유효한 경우 사용자의 인증 정보를 SecurityContext에 설정함
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 다음 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
     */
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val log: Logger = LoggerFactory.getLogger(JWTFilter::class.java)

        // 로컬 로그인 Access Token
        var accessToken = request.getHeader("accessToken")
        println(accessToken)

        // OAuth Access Token
        if (accessToken == null) {
            accessToken = tokenService.getOAuthAccessToken(request)
        }

        // OAuth 로그인 시 데이터
        val naverCode = request.getParameter("code")
        val googleAuthInfo = request.getParameter("Authorization")

        // Access Token이 없거나 OAuth 로그인이 되어 있지 않다면 다음 필터로 넘김
        if (accessToken == null && naverCode == null && googleAuthInfo == null) {
            log.info("[Access Token이 없는 사용자의 요청] 접근 URL : {}", request.requestURL)
            request.setAttribute("exception", CommonForbiddenException("Access Token이 존재하지 않습니다."))
            filterChain.doFilter(request, response)
        } else {
            try {
                // Access Token에 담긴 사용자 정보
                val userEntity = tokenService.getUserByAccessToken(accessToken!!).get()

                var authToken: Authentication? = null

                // 토큰의 사용자 정보를 추출해 UsernamePasswordAuthenticationToken을 생성하여 인증 객체 설정
                if (tokenService.checkCategoryFromAccessToken(accessToken, TokenCategory.access.toString())) {
                    val customUserDetails = CustomUserDetails(userEntity)
                    authToken = UsernamePasswordAuthenticationToken(
                        customUserDetails, null,
                        customUserDetails.authorities
                    )
                } else if (tokenService.checkCategoryFromAccessToken(accessToken, TokenCategory.OAuth.toString())) {
                    val oAuthUser = tokenService.getUserByAccessToken(accessToken)
                    if (oAuthUser.isPresent) {
                        val customUserDetails = CustomUserDetails(oAuthUser.get())
                        authToken =
                            UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.authorities)
                    }
                }
                // 세션에 사용자 등록
                SecurityContextHolder.getContext().authentication = authToken
            } catch (e: ExpiredJwtException) {
                log.error(e.message)
                request.setAttribute("exception", e)
                filterChain.doFilter(request, response)
            } catch (e: Exception) {
                log.error(e.message)
                request.setAttribute("exception", CommonForbiddenException("잘못된 접근입니다."))
                filterChain.doFilter(request, response)
            }

            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response)
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path == "/users/signin" || path == "/users/signup" // /users/signin과 /users/signup 경로는 필터링 제외
    }
}
