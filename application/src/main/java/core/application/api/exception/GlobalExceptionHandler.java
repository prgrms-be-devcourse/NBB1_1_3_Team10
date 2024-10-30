package core.application.api.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import core.application.api.response.ApiResponse;
import core.application.api.response.code.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(CommonForbiddenException.class)
	public ApiResponse<?> handleCommonForbiddenException(CommonForbiddenException e) {
		log.error(e.getMessage());
		return ApiResponse.onFailure(ErrorStatus.FORBIDDEN.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(InvalidLoginException.class)
	public ApiResponse<?> handleInvalidLoginException(InvalidLoginException e) {
		log.error(e.getMessage());
		return ApiResponse.onFailure(ErrorStatus.INVALID_LOGIN.getCode(), e.getMessage(), null);
	}
}
