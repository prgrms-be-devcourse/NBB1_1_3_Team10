package core.application.security.exception

import core.application.api.response.ApiResponse
import core.application.api.response.code.status.ErrorStatus
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Slf4j
@RestControllerAdvice(basePackages = ["core.application.users"])
class SecurityExceptionHandler {
    private val log: Logger = LoggerFactory.getLogger(SecurityExceptionHandler::class.java)

    @ExceptionHandler(InvalidTokenCategoryException::class)
    fun handleInvalidTokenCategoryException(e: InvalidTokenCategoryException): ApiResponse<*> {
        log.error("잘못된 토큰 유형")
        return ApiResponse.onFailure<Any?>(ErrorStatus.INVALID_TOKEN_CATEGORY.code, e.message, null)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(e: InvalidTokenException): ApiResponse<*> {
        log.error("유효하지 않은 토큰")
        return ApiResponse.onFailure<Any?>(ErrorStatus.INVALID_TOKEN.code, e.message, null)
    }

    @ExceptionHandler(RefreshTokenNotFoundException::class)
    fun handleRefreshTokenNotFoundException(e: RefreshTokenNotFoundException): ApiResponse<*> {
        log.error("찾을 수 없는 Refresh Token")
        return ApiResponse.onFailure<Any?>(ErrorStatus.NOT_FOUND_REFRESHTOKEN.code, e.message, null)
    }

    @ExceptionHandler(UnauthorizedUserException::class)
    fun handleUnauthorizedUserException(e: UnauthorizedUserException): ApiResponse<*> {
        log.error("인증되지 않은 사용자")
        return ApiResponse.onFailure<Any?>(ErrorStatus.UNAUTHORIZED_USER.code, e.message, null)
    }

    @ExceptionHandler(ValueNotFoundException::class)
    fun handleValueNotFoundException(e: ValueNotFoundException): ApiResponse<*> {
        log.error("key에 해당하는 value가 존재하지 않음")
        return ApiResponse.onFailure<Any?>(ErrorStatus.NOT_FOUND_VALUE.code, e.message, null)
    }
}
