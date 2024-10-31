package core.application.security.token

import core.application.security.exception.InvalidTokenCategoryException
import core.application.security.exception.InvalidTokenException
import core.application.security.model.TokenCategory
import core.application.users.exception.UserNotFoundException
import core.application.users.models.entities.UserEntity
import core.application.users.service.UserService
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.util.*

/**
 * JWT 토큰을 관리하는 서비스 클래스
 *
 * 액세스 토큰과 리프레시 토큰을 검증, 사용자 정보를 추출,
 * 새로운 액세스 토큰을 재발급하는 기능 제공
 */
@Service
class TokenService
/**
 * TokenService의 생성자
 *
 * @param jwtUtil JWT 유틸리티 클래스
 * @param userService 사용자 관련 서비스
 * @param redisService Redis 관련 서비스
 */ internal constructor(
    private val jwtUtil: JwtTokenUtil,
    private val userService: UserService,
    private val redisService: RedisService
) {
    /**
     * HTTP 요청에서 리프레시 토큰을 가져옴
     *
     * @param request HTTP 요청 객체
     * @return 리프레시 토큰 문자열
     */
    fun getRefreshToken(request: HttpServletRequest): String? {
        var refreshToken: String? = null
        val cookies = request.cookies
        for (cookie in cookies) {
            if (cookie.name == "refreshToken") {
                refreshToken = cookie.value
            }
        }
        return refreshToken
    }

    /**
     * 주어진 리프레시 토큰의 유효성을 검증함
     *
     * @param refreshToken 리프레시 토큰 문자열
     * @return 유효성 검사 결과
     */
    fun isRefreshTokenValid(refreshToken: String): Boolean {
        if (refreshToken == null) {
            throw InvalidTokenException("Refresh Token이 없습니다.")
        }

        try {
            jwtUtil.isExpired(refreshToken)
        } catch (e: ExpiredJwtException) {
            throw InvalidTokenException("만료된 Refresh Token 입니다.")
        }

        val category = jwtUtil.getCategory(refreshToken)

        if (category != "refresh") {
            throw InvalidTokenCategoryException("잘못된 토큰 유형입니다: Refresh Token이 아닙니다.")
        }

        if (redisService.getValue(jwtUtil.getUserEmail(refreshToken)) == null) {
            throw InvalidTokenException("유효하지 않은 Refresh Token 입니다.")
        }
        return true
    }

    /**
     * HTTP 요청에서 OAuth 토큰을 가져옴
     *
     * @param request HTTP 요청 객체
     * @return 리프레시 토큰 문자열
     */
    fun getOAuthAccessToken(request: HttpServletRequest): String? {
        var accessToken: String? = null
        val cookies = request.cookies
        if (cookies != null) { // 쿠키가 null인지 확인
            for (cookie in cookies) {
                if ("accessToken" == cookie.name) {
                    accessToken = cookie.value
                    break
                }
            }
        }
        return accessToken
    }

    /**
     * 주어진 액세스 토큰의 유효성 검증
     *
     * @param accessToken 액세스 토큰 문자열
     * @return 유효성 검사 결과
     */
    fun isAccessTokenValid(accessToken: String): Boolean {
        if (accessToken == null) {
            throw InvalidTokenException("Access Token이 없습니다.")
        }

        try {
            jwtUtil.isExpired(accessToken)
        } catch (e: ExpiredJwtException) {
            throw InvalidTokenException("만료된 Access Token 입니다.")
        }

        val category = jwtUtil.getCategory(accessToken)

        if (category != "access" && category != "OAuth") {
            throw InvalidTokenCategoryException("잘못된 토큰 유형입니다: Access Token이 아닙니다.")
        }
        return true
    }

    /**
     * Access Token 유형 확인
     *
     * @param accessToken, category
     * @return 카테고리 일치 여부
     */
    fun checkCategoryFromAccessToken(accessToken: String, category: String): Boolean {
        if (!isAccessTokenValid(accessToken)) {
            throw InvalidTokenException("유효하지 않은 Access Token 입니다.")
        }
        return jwtUtil.getCategory(accessToken) == category
    }

    /**
     * 주어진 액세스 토큰으로부터 사용자 정보 추출
     *
     * @param accessToken 액세스 토큰 문자열
     * @return [Optional]`<`[UserEntity]`>`
     * 사용자 정보가 포함된 Optional 객체
     */
    fun getUserByAccessToken(accessToken: String): Optional<UserEntity> {
        if (!isAccessTokenValid(accessToken)) {
            throw InvalidTokenException("유효하지 않은 Access Token 입니다.")
        }

        val userEmail = jwtUtil.getUserEmail(accessToken)
        val userEntity = userService.getUserByUserEmail(userEmail)

        // 주요한 정보 제외한 UserEntity 반환
        return userEntity!!.map { entity: UserEntity? ->
            UserEntity(
                entity!!.userId,
                entity.userEmail,
                null,
                entity.role,
                entity.alias,
                null,
                entity.userName
            )
        }
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 재발급
     *
     * @param request HTTP 요청 객체
     * @return 새로 발급된 액세스 토큰 문자열
     */
    fun reissueAccessToken(request: HttpServletRequest): String {
        val refreshToken = getRefreshToken(request)

        if (isRefreshTokenValid(refreshToken!!)) {
            val userEmail = jwtUtil.getUserEmail(refreshToken)
            val userEntity = userService.getUserByUserEmail(userEmail)

            if (userEntity!!.isPresent) {
                val userId = userEntity.get().userId
                val role = userEntity.get().role.toString()

                return jwtUtil.creatAccessToken(userEmail, userId, role, TokenCategory.access.toString())
            } else {
                throw UserNotFoundException("사용자를 찾을 수 없습니다.")
            }
        } else {
            throw InvalidTokenException("유효하지 않은 Refresh Token 입니다.")
        }
    }

    /**
     * 주어진 리프레시 토큰 비활성화
     *
     * @param request HTTP 요청 객체
     */
    fun inactiveRefreshToken(request: HttpServletRequest) {
        val accessToken = request.getHeader("accessToken")
        val email = jwtUtil.getUserEmail(accessToken)
        if (isAccessTokenValid(accessToken)) {
            redisService.deleteValue(email)
        }
    }
}
