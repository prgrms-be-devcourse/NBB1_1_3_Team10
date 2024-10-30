package core.application.movies.exception;

public class InvalidReactionException extends RuntimeException {
	public InvalidReactionException(String message) {
		super(message);
	}

	public InvalidReactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidReactionException(Throwable cause) {
		super(cause);
	}

	public InvalidReactionException() {
		super();
	}
}
