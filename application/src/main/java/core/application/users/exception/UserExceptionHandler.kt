package core.application.users.exception

import core.application.api.response.ApiResponse
import core.application.api.response.code.status.ErrorStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Slf4j
@RestControllerAdvice(basePackages = ["core.application.users"])
class UserExceptionHandler {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmailException(e: DuplicateEmailException): ApiResponse<*> {
        log.error("중복된 EMAIL 회원가입 시도")
        return ApiResponse.onFailure<Any?>(ErrorStatus.DUPLICATE_EMAIL.code, e.message, null)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(e: UserNotFoundException): ApiResponse<*> {
        log.error("찾을 수 없는 사용자")
        return ApiResponse.onFailure<Any?>(ErrorStatus.NOT_FOUND_USER.code, e.message, null)
    }
}
