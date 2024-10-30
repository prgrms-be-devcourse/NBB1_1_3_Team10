package core.application.movies.exception;

public class NotCommentWriterException extends RuntimeException {
	public NotCommentWriterException() {
		super();
	}

	public NotCommentWriterException(String message) {
		super(message);
	}

	public NotCommentWriterException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotCommentWriterException(Throwable cause) {
		super(cause);
	}
}
