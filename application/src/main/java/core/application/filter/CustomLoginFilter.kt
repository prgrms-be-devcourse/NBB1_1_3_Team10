package core.application.filter

import core.application.api.exception.InvalidLoginException
import core.application.api.response.ApiResponse
import core.application.api.response.code.Message
import core.application.security.auth.CustomUserDetails
import core.application.security.model.TokenCategory
import core.application.security.token.JwtTokenUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * 사용자 로그인 요청을 처리하는 커스텀 필터
 * Spring Security의 UsernamePasswordAuthenticationFilter를 확장하여
 * JSON 형식의 로그인 요청을 처리하고 JWT 발급
 */
@Slf4j
class CustomLoginFilter(private val authenticationManager: AuthenticationManager, private val jwtUtil: JwtTokenUtil) :
    UsernamePasswordAuthenticationFilter() {
    /**
     * 생성자.
     *
     * @param authenticationManager 인증 매니저
     * @param jwtUtil JWT 관련 유틸리티
     */
    init {
        setFilterProcessesUrl("/users/signin")
    }

    /**
     * 인증을 시도합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 인증된 Authentication 객체
     * @throws AuthenticationException 인증 중 발생한 예외
     */
    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val log: Logger = LoggerFactory.getLogger(CustomLoginFilter::class.java);

        try {
            // InputStream에서 JSON 문자열을 UTF-8로 읽음
            val jsonInput = String(request.inputStream.readAllBytes(), StandardCharsets.UTF_8)

            // JSON 객체 생성
            val jsonObject = JSONObject(jsonInput)

            // userEmail과 userPw 추출
            val userEmail = jsonObject.getString("userEmail")
            val userPw = jsonObject.getString("userPw")

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담음
            val authToken = UsernamePasswordAuthenticationToken(userEmail, userPw, null)

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken)
        } catch (e: AuthenticationException) {
            // 인증 과정에서 발생한 예외를 로그로 남김
            log.info("Authentication Failed:{}", e.message)
            request.setAttribute("exception", InvalidLoginException("잘못된 아이디 또는 비밀번호입니다."))
            throw InvalidLoginException("잘못된 아이디 또는 비밀번호입니다.")
        } catch (e: IOException) {
            // JSON 파싱 또는 IO 오류 처리
            log.info("Invalid request format: {}", e.message)
            request.setAttribute("exception", InvalidLoginException("잘못된 형식입니다."))
            throw InvalidLoginException("잘못된 형식입니다.")
        } catch (e: JSONException) {
            log.info("Invalid request format: {}", e.message)
            request.setAttribute("exception", InvalidLoginException("잘못된 형식입니다."))
            throw InvalidLoginException("잘못된 형식입니다.")
        }
    }

    /**
     * 쿠키를 생성합니다.
     *
     * @param value 쿠키의 값
     * @return 생성된 Cookie 객체
     */
    private fun createCookie(value: String): Cookie {
        val cookie = Cookie("refreshToken", value)
        cookie.maxAge = 14 * 24 * 60 * 60 // 쿠키의 최대 수명 설정
        cookie.isHttpOnly = true // JavaScript에서 접근할 수 없도록 설정
        return cookie
    }

    /**
     * 로그인 성공 시 호출되는 메소드
     * JWT를 발급하고 응답에 추가함
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param chain 필터 체인
     * @param authentication 인증된 사용자 정보
     */
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        val customUserDetails = authentication.principal as CustomUserDetails

        val userEmail = customUserDetails.userEntity.userEmail
        val userId = customUserDetails.userEntity.userId

        val authorities = authentication.authorities
        val iterator: Iterator<GrantedAuthority> = authorities.iterator()
        val auth = iterator.next()

        val role = auth.authority

        // token 발급
        val accessToken = jwtUtil.creatAccessToken(userEmail, userId, role, TokenCategory.access.toString())
        val refreshToken = jwtUtil.creatRefreshToken(userEmail, TokenCategory.refresh.toString())

        response.setHeader("accessToken", accessToken) // 액세스 토큰을 응답 헤더에 추가
        response.addCookie(createCookie(refreshToken)) // 리프레시 토큰을 쿠키에 추가
        ApiResponse.onCreateSuccess(Message.createMessage("Access Token, Refresh Token을 생성 성공했습니다."))
    }

    /**
     * 로그인 실패 시 호출되는 메소드.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param failed 인증 실패 정보
     */
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
    }
}
