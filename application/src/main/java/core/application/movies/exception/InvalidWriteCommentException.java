package core.application.movies.exception;

public class InvalidWriteCommentException extends RuntimeException {
	public InvalidWriteCommentException(String message) {
		super(message);
	}

	public InvalidWriteCommentException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidWriteCommentException(Throwable cause) {
		super(cause);
	}

	public InvalidWriteCommentException() {
		super();
	}
}
