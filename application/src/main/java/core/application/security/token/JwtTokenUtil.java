package core.application.security.token;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT(JSON Web Token) 관련 유틸리티 클래스
 * JWT 생성, 검증 및 관련 정보 추출 기능을 제공
 */
@Component
public class JwtTokenUtil {

    private final SecretKey secretKey;
    private final Long accessTimeout;
    private final Long refreshTimeout;
    private final RedisService redisService;

    /**
     * 생성자
     *
     * @param secret JWT 서명에 사용할 비밀 키
     * @param accessTimeout 액세스 토큰의 만료 시간(밀리초)
     * @param refreshTimeout 리프레시 토큰의 만료 시간(일 수)
     */
    public JwtTokenUtil(@Value("${spring.jwt.secret}") String secret,
                        @Value("${token.access.timeout}") Long accessTimeout,
                        @Value("${token.refresh.timeout}") Long refreshTimeout,
                        RedisService redisService) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTimeout = accessTimeout;
        this.refreshTimeout = refreshTimeout;
        this.redisService = redisService;
    }

    /**
     * 주어진 토큰에서 사용자 이메일 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String getUserEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userEmail", String.class);
    }

    /**
     * 주어진 토큰에서 카테고리 추출
     *
     * @param token JWT 토큰
     * @return 카테고리
     */
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    /**
     * 주어진 토큰의 만료 여부 확인
     *
     * @param token JWT 토큰
     * @return 만료 여부
     */
    public boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * 사용자 이메일, 사용자 ID, 역할 및 카테고리 정보를 사용하여 액세스 토큰 생성
     *
     * @param userEmail 사용자 이메일
     * @param userId 사용자 ID
     * @param role 사용자 역할
     * @param category 토큰 카테고리 ("access", "refresh")
     * @return 생성된 액세스 토큰
     */
    public String creatAccessToken(String userEmail, UUID userId, String role, String category) {
        return Jwts.builder()
                .claim("userEmail", userEmail)
                .claim("userId", userId)
                .claim("role", role)
                .claim("category", category)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTimeout))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 사용자 이메일과 카테고리를 사용하여 리프레시 토큰 생성
     *
     * @param userEmail 사용자 이메일
     * @param category 토큰 카테고리 ("access", "refresh")
     * @return 생성된 리프레시 토큰
     */
    public String creatRefreshToken(String userEmail, String category) {
        String refreshToken = Jwts.builder()
                .claim("userEmail", userEmail)
                .claim("category", category)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTimeout * 24 * 60 * 60 * 1000L))
                .signWith(secretKey)
                .compact();
        redisService.setValueWithTTL(userEmail, refreshToken); // Redis에 리프레시 토큰 저장
        return refreshToken;
    }
}
