package core.application.security.token;

import core.application.security.exception.InvalidTokenCategoryException;
import core.application.security.exception.InvalidTokenException;
import core.application.security.model.TokenCategory;
import core.application.users.exception.UserNotFoundException;
import core.application.users.models.entities.UserEntity;
import core.application.users.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * JWT 토큰을 관리하는 서비스 클래스
 *
 * 액세스 토큰과 리프레시 토큰을 검증, 사용자 정보를 추출,
 * 새로운 액세스 토큰을 재발급하는 기능 제공
 */
@Service
public class TokenService {
    private final JwtTokenUtil jwtUtil;
    private final UserService userService;
    private final RedisService redisService;

    /**
     * TokenService의 생성자
     *
     * @param jwtUtil JWT 유틸리티 클래스
     * @param userService 사용자 관련 서비스
     * @param redisService Redis 관련 서비스
     */
    TokenService(JwtTokenUtil jwtUtil, UserService userService, RedisService redisService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.redisService = redisService;
    }

    /**
     * HTTP 요청에서 리프레시 토큰을 가져옴
     *
     * @param request HTTP 요청 객체
     * @return 리프레시 토큰 문자열
     */
    public String getRefreshToken(HttpServletRequest request) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }
        return refreshToken;
    }

    /**
     * 주어진 리프레시 토큰의 유효성을 검증함
     *
     * @param refreshToken 리프레시 토큰 문자열
     * @return 유효성 검사 결과
     */
    public Boolean isRefreshTokenValid(String refreshToken) {
        if (refreshToken == null) {
            throw new InvalidTokenException("Refresh Token이 없습니다.");
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("만료된 Refresh Token 입니다.");
        }

        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {
            throw new InvalidTokenCategoryException("잘못된 토큰 유형입니다: Refresh Token이 아닙니다.");
        }

        if (redisService.getValue(jwtUtil.getUserEmail(refreshToken)) == null) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token 입니다.");
        }
        return true;
    }

    /**
     * HTTP 요청에서 OAuth 토큰을 가져옴
     *
     * @param request HTTP 요청 객체
     * @return 리프레시 토큰 문자열
     */
    public String getOAuthAccessToken(HttpServletRequest request) {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) { // 쿠키가 null인지 확인
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        return accessToken;
    }

    /**
     * 주어진 액세스 토큰의 유효성 검증
     *
     * @param accessToken 액세스 토큰 문자열
     * @return 유효성 검사 결과
     */
    public Boolean isAccessTokenValid(String accessToken) {
        if (accessToken == null) {
            throw new InvalidTokenException("Access Token이 없습니다.");
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("만료된 Access Token 입니다.");
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access") && !category.equals("OAuth")) {
            throw new InvalidTokenCategoryException("잘못된 토큰 유형입니다: Access Token이 아닙니다.");
        }
        return true;
    }

    /**
     * Access Token 유형 확인
     *
     * @param accessToken, category
     * @return 카테고리 일치 여부
     */
    public Boolean checkCategoryFromAccessToken(String accessToken, String category) {
        if (!isAccessTokenValid(accessToken)) {
            throw new InvalidTokenException("유효하지 않은 Access Token 입니다.");
        }
        return jwtUtil.getCategory(accessToken).equals(category);
    }

    /**
     * 주어진 액세스 토큰으로부터 사용자 정보 추출
     *
     * @param accessToken 액세스 토큰 문자열
     * @return {@link Optional}{@code <}{@link UserEntity}{@code >}
     *         사용자 정보가 포함된 Optional 객체
     */
    public Optional<UserEntity> getUserByAccessToken(String accessToken) {
        if (!isAccessTokenValid(accessToken)) {
            throw new InvalidTokenException("유효하지 않은 Access Token 입니다.");
        }

        String userEmail = jwtUtil.getUserEmail(accessToken);
        Optional<UserEntity> userEntity = userService.getUserByUserEmail(userEmail);

        // 주요한 정보 제외한 UserEntity 반환
        return userEntity.map(entity -> new UserEntity(
                entity.getUserId(),
                entity.getUserEmail(),
                entity.getAlias(),
                entity.getUserName(),
                entity.getRole()
        ));
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 재발급
     *
     * @param request HTTP 요청 객체
     * @return 새로 발급된 액세스 토큰 문자열
     */
    public String reissueAccessToken(HttpServletRequest request) {
        String refreshToken = getRefreshToken(request);

        if (isRefreshTokenValid(refreshToken)) {
            String userEmail =  jwtUtil.getUserEmail(refreshToken);
            Optional<UserEntity> userEntity = userService.getUserByUserEmail(userEmail);

            if (userEntity.isPresent()) {
                UUID userId = userEntity.get().getUserId();
                String role = userEntity.get().getRole().toString();

                return jwtUtil.creatAccessToken(userEmail, userId, role, TokenCategory.access.toString());
            } else {
                throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
            }
        } else {
            throw new InvalidTokenException("유효하지 않은 Refresh Token 입니다.");
        }
    }

    /**
     * 주어진 리프레시 토큰 비활성화
     *
     * @param request HTTP 요청 객체
     */
    public void inactiveRefreshToken(HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        String email = jwtUtil.getUserEmail(accessToken);
        if (isAccessTokenValid(accessToken)) {
            redisService.deleteValue(jwtUtil.getUserEmail(email));
        }
    }
}
