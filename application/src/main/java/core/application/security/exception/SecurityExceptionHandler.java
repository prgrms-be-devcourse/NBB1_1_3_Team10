package core.application.security.exception;

import core.application.api.response.ApiResponse;
import core.application.api.response.code.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "core.application.users")
public class SecurityExceptionHandler {

	@ExceptionHandler(InvalidTokenCategoryException.class)
	public ApiResponse<?> handleInvalidTokenCategoryException(InvalidTokenCategoryException e) {
		log.error("잘못된 토큰 유형");
		return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN_CATEGORY.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ApiResponse<?> handleInvalidTokenException(InvalidTokenException e) {
		log.error("유효하지 않은 토큰");
		return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(RefreshTokenNotFoundException.class)
	public ApiResponse<?> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
		log.error("찾을 수 없는 Refresh Token");
		return ApiResponse.onFailure(ErrorStatus.NOT_FOUND_REFRESHTOKEN.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(UnauthorizedUserException.class)
	public ApiResponse<?> handleUnauthorizedUserException(UnauthorizedUserException e) {
		log.error("인증되지 않은 사용자");
		return ApiResponse.onFailure(ErrorStatus.UNAUTHORIZED_USER.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(ValueNotFoundException.class)
	public ApiResponse<?> handleValueNotFoundException(ValueNotFoundException e) {
		log.error("key에 해당하는 value가 존재하지 않음");
		return ApiResponse.onFailure(ErrorStatus.NOT_FOUND_VALUE.getCode(), e.getMessage(), null);
	}
}
