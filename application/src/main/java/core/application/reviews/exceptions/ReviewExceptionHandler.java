package core.application.reviews.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import core.application.api.response.ApiResponse;
import core.application.api.response.code.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "core.application.reviews")
public class ReviewExceptionHandler {

	@ExceptionHandler(NoReviewFoundException.class)
	public ApiResponse<?> handleNoReviewFoundException(NoReviewFoundException e) {
		logHandledException("handleNoReviewFoundException", e);
		return ApiResponse.onFailure(ErrorStatus.NO_REVIEW.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(NoReviewCommentFoundException.class)
	public ApiResponse<?> handleNoReviewCommentFoundException(NoReviewCommentFoundException e) {
		logHandledException("handleNoReviewCommentFoundException", e);
		return ApiResponse.onFailure(ErrorStatus.NO_REVIEW_COMMENT.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(NotCommentOwnerException.class)
	public ApiResponse<?> handleNotCommentOwnerException(NotCommentOwnerException e) {
		logHandledException("handleNotCommentOwnerException", e);
		return ApiResponse.onFailure(ErrorStatus.NOT_COMMENT_OWNER.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(InvalidCommentContentException.class)
	public ApiResponse<?> handleInvalidCommentContentException(InvalidCommentContentException e) {
		logHandledException("handleInvalidCommentContentException", e);
		return ApiResponse.onFailure(ErrorStatus.INVALID_COMMENT_CONTENT.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(InvalidPageException.class)
	public ApiResponse<?> handleInvalidCommentContentException(InvalidPageException e) {
		logHandledException("handleInvalidCommentContentException", e);
		return ApiResponse.onFailure(ErrorStatus.INVALID_PAGE.getCode(), e.getMessage(), null);
	}

	private void logHandledException(String methodName, Throwable e) {
		String stackTrace = getStackTraceToString(e);
		log.warn("[ReviewExceptionHandler - {}] handled exception : {}", methodName,
			e.getMessage());
		log.debug(e.getMessage(), e);
	}

	private String getStackTraceToString(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	@Data
	@AllArgsConstructor
	public static class ResponseError {

		private String message;
		private HttpStatus status;
		private String stackTrace;
	}
}
