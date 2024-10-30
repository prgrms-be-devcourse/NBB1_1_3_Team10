package core.application.movies.exception;

public class NoSearchResultException extends RuntimeException {
	public NoSearchResultException(String message) {
		super(message);
	}

	public NoSearchResultException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSearchResultException(Throwable cause) {
		super(cause);
	}

	public NoSearchResultException() {
		super();
	}
}
