package core.application.movies.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import core.application.api.response.ApiResponse;
import core.application.api.response.code.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "core.application.movies.controller")
public class MovieExceptionHandler {

	@ExceptionHandler(NoMovieException.class)
	public ApiResponse<?> handleNoMoviesException(NoMovieException e) {
		log.error("KMDB API를 이용한 검색 결과 아무것도 나오지 않음.");
		// 프론트에서 리턴 타입에 따라 처리가 가능할까?
		return ApiResponse.onFailure(ErrorStatus.NO_MOVIE.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(NotFoundUrlException.class)
	public ApiResponse<?> handleNotFoundUrlException(NotFoundUrlException e) {
		log.error("잘못된 페이지 접근");
		return ApiResponse.onFailure(ErrorStatus.NOT_FOUND.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(NotFoundCommentException.class)
	public ApiResponse<?> handleNotFoundCommentException(NotFoundCommentException e) {
		log.error(e.getMessage());
		return ApiResponse.onFailure(ErrorStatus.NOT_FOUND_COMMENT.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(InvalidReactionException.class)
	public ApiResponse<?> handleInvalidReactionException(InvalidReactionException e) {
		log.error(e.getMessage());
		return ApiResponse.onFailure(ErrorStatus.INVALID_REACTION.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(InvalidWriteCommentException.class)
	public ApiResponse<?> handleInvalidWriteCommentException(InvalidWriteCommentException e) {
		log.error(e.getMessage());
		return ApiResponse.onFailure(ErrorStatus.INVALID_WRITE_COMMENT.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(NoSearchResultException.class)
	public ApiResponse<?> handleNoSearchResultException(NoSearchResultException e) {
		log.error(e.getMessage());
		return ApiResponse.onFailure(ErrorStatus.NO_SEARCH_RESULT.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(NotCommentWriterException.class)
	public ApiResponse<?> handleNotCommentWriterException(NotCommentWriterException e) {
		log.error(e.getMessage());
		return ApiResponse.onFailure(ErrorStatus.NOT_COMMENT_WRITER.getCode(), e.getMessage(), null);
	}

	@ExceptionHandler(NotMatchMovieCommentException.class)
	public ApiResponse<?> handleNotMatchMovieCommentException(NotMatchMovieCommentException e) {
		log.error(e.getMessage());
		return ApiResponse.onFailure(ErrorStatus.NOT_MATCH_MOVIECOMMENT.getCode(), e.getMessage(), null);
	}
}
