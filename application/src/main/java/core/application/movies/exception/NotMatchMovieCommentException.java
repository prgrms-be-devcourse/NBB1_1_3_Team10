package core.application.movies.exception;

public class NotMatchMovieCommentException extends RuntimeException {
	public NotMatchMovieCommentException() {
		super();
	}

	public NotMatchMovieCommentException(String message) {
		super(message);
	}

	public NotMatchMovieCommentException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotMatchMovieCommentException(Throwable cause) {
		super(cause);
	}
}
