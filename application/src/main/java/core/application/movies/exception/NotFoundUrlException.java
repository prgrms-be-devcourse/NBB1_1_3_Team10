package core.application.movies.exception;

public class NotFoundUrlException extends RuntimeException {
	public NotFoundUrlException() {
		super();
	}

	public NotFoundUrlException(String message) {
		super(message);
	}

	public NotFoundUrlException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundUrlException(Throwable cause) {
		super(cause);
	}
}
