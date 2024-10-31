package core.application.filter;

import core.application.api.exception.InvalidLoginException;
import core.application.api.response.ApiResponse;
import core.application.api.response.code.Message;
import core.application.security.token.JwtTokenUtil;
import core.application.security.auth.CustomUserDetails;
import core.application.security.model.TokenCategory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

/**
 * 사용자 로그인 요청을 처리하는 커스텀 필터
 * Spring Security의 UsernamePasswordAuthenticationFilter를 확장하여
 * JSON 형식의 로그인 요청을 처리하고 JWT 발급
 */
@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtil;

    /**
     * 생성자.
     *
     * @param authenticationManager 인증 매니저
     * @param jwtUtil JWT 관련 유틸리티
     */
    public CustomLoginFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/users/signin");
    }

    /**
     * 인증을 시도합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 인증된 Authentication 객체
     * @throws AuthenticationException 인증 중 발생한 예외
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // InputStream에서 JSON 문자열을 UTF-8로 읽음
            String jsonInput = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            // JSON 객체 생성
            JSONObject jsonObject = new JSONObject(jsonInput);

            // userEmail과 userPw 추출
            String userEmail = jsonObject.getString("userEmail");
            String userPw = jsonObject.getString("userPw");

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담음
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, userPw, null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);
        } catch (AuthenticationException e) {
            // 인증 과정에서 발생한 예외를 로그로 남김
            log.info("Authentication Failed:{}", e.getMessage());
            request.setAttribute("exception", new InvalidLoginException("잘못된 아이디 또는 비밀번호입니다."));
            throw new InvalidLoginException("잘못된 아이디 또는 비밀번호입니다.");
        } catch (IOException | JSONException e) {
            // JSON 파싱 또는 IO 오류 처리
            log.info("Invalid request format: {}", e.getMessage());
            request.setAttribute("exception", new InvalidLoginException("잘못된 형식입니다."));
            throw new InvalidLoginException("잘못된 형식입니다.");
        }
    }

    /**
     * 쿠키를 생성합니다.
     *
     * @param value 쿠키의 값
     * @return 생성된 Cookie 객체
     */
    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("refreshToken", value);
        cookie.setMaxAge(14*24*60*60); // 쿠키의 최대 수명 설정
        cookie.setHttpOnly(true); // JavaScript에서 접근할 수 없도록 설정
        return cookie;
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
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userEmail = customUserDetails.userEntity.getUserEmail();
        UUID userId = customUserDetails.userEntity.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // token 발급
        String accessToken = jwtUtil.creatAccessToken(userEmail, userId, role, TokenCategory.access.toString());
        String refreshToken = jwtUtil.creatRefreshToken(userEmail, TokenCategory.refresh.toString());

        response.setHeader("accessToken", accessToken); // 액세스 토큰을 응답 헤더에 추가
        response.addCookie(createCookie(refreshToken)); // 리프레시 토큰을 쿠키에 추가
        ApiResponse.onCreateSuccess(Message.createMessage("Access Token, Refresh Token을 생성 성공했습니다."));
    }

    /**
     * 로그인 실패 시 호출되는 메소드.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param failed 인증 실패 정보
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    }
}
