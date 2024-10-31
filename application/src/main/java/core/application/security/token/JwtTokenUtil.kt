package core.application.security.token

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * JWT(JSON Web Token) 관련 유틸리티 클래스
 * JWT 생성, 검증 및 관련 정보 추출 기능을 제공
 */
@Component
class JwtTokenUtil(
    @Value("\${spring.jwt.secret}") secret: String,
    @param:Value("\${token.access.timeout}") private val accessTimeout: Long,
    @param:Value("\${token.refresh.timeout}") private val refreshTimeout: Long,
    private val redisService: RedisService
) {
    private val secretKey: SecretKey =
        SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().algorithm)

    /**
     * 주어진 토큰에서 사용자 이메일 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    fun getUserEmail(token: String?): String {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.get(
            "userEmail",
            String::class.java
        )
    }

    /**
     * 주어진 토큰에서 카테고리 추출
     *
     * @param token JWT 토큰
     * @return 카테고리
     */
    fun getCategory(token: String?): String {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.get(
            "category",
            String::class.java
        )
    }

    /**
     * 주어진 토큰의 만료 여부 확인
     *
     * @param token JWT 토큰
     * @return 만료 여부
     */
    fun isExpired(token: String?): Boolean {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.expiration.before(Date())
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
    fun creatAccessToken(userEmail: String?, userId: UUID?, role: String?, category: String?): String {
        return Jwts.builder()
            .claim("userEmail", userEmail)
            .claim("userId", userId)
            .claim("role", role)
            .claim("category", category)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + accessTimeout))
            .signWith(secretKey)
            .compact()
    }

    /**
     * 사용자 이메일과 카테고리를 사용하여 리프레시 토큰 생성
     *
     * @param userEmail 사용자 이메일
     * @param category 토큰 카테고리 ("access", "refresh")
     * @return 생성된 리프레시 토큰
     */
    fun creatRefreshToken(userEmail: String?, category: String?): String {
        val refreshToken = Jwts.builder()
            .claim("userEmail", userEmail)
            .claim("category", category)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + refreshTimeout * 24 * 60 * 60 * 1000L))
            .signWith(secretKey)
            .compact()
        redisService.setValueWithTTL(userEmail, refreshToken) // Redis에 리프레시 토큰 저장
        return refreshToken
    }
}
