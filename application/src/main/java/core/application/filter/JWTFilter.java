package core.application.filter;

import core.application.api.exception.CommonForbiddenException;
import core.application.security.model.TokenCategory;
import core.application.users.models.entities.UserEntity;
import core.application.security.auth.CustomUserDetails;
import core.application.security.token.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * JWT 기반의 인증을 처리하는 필터
 *
 * HTTP 요청에서 Access Token을 추출하여
 * 사용자의 인증 정보를 SecurityContext에 설정
 */
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    TokenService tokenService;

    /**
     * JWTFilter 생성자
     *
     * @param tokenService JWT와 관련된 사용자 정보를 처리하는 서비스
     */
    public JWTFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

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
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 로컬 로그인 Access Token
        String accessToken = request.getHeader("accessToken");
        System.out.println(accessToken);

        // OAuth Access Token
        if (accessToken == null) {
            accessToken = tokenService.getOAuthAccessToken(request);
        }

        // OAuth 로그인 시 데이터
        String naverCode = request.getParameter("code");
        String googleAuthInfo = request.getParameter("Authorization");

        // Access Token이 없거나 OAuth 로그인이 되어 있지 않다면 다음 필터로 넘김
        if (accessToken == null && naverCode == null && googleAuthInfo == null) {
            log.info("[Access Token이 없는 사용자의 요청] 접근 URL : {}", request.getRequestURL());
            request.setAttribute("exception", new CommonForbiddenException("Access Token이 존재하지 않습니다."));
            filterChain.doFilter(request, response);
        }
        else {
            try {
                // Access Token에 담긴 사용자 정보
                UserEntity userEntity = tokenService.getUserByAccessToken(accessToken).get();

                Authentication authToken = null;

                // 토큰의 사용자 정보를 추출해 UsernamePasswordAuthenticationToken을 생성하여 인증 객체 설정
                if (tokenService.checkCategoryFromAccessToken(accessToken, TokenCategory.access.toString())) {
                    CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
                    authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
                            customUserDetails.getAuthorities());
                }

                // 토큰의 사용자 정보를 추출해 UsernamePasswordAuthenticationToken을 생성하여 인증 객체 설정
                else if (tokenService.checkCategoryFromAccessToken(accessToken, TokenCategory.OAuth.toString())) {
                    Optional<UserEntity> oAuthUser = tokenService.getUserByAccessToken(accessToken);
                    if (oAuthUser.isPresent()) {
                        CustomUserDetails customUserDetails = new CustomUserDetails(oAuthUser.get());
                        authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                    }
                }
                // 세션에 사용자 등록
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (ExpiredJwtException e) {
                log.error(e.getMessage());
                request.setAttribute("exception", e);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.error(e.getMessage());
                request.setAttribute("exception", new CommonForbiddenException("잘못된 접근입니다."));
                filterChain.doFilter(request, response);
            }

            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);
        }
    }
}
