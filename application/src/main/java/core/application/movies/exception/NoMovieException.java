package core.application.movies.exception;

public class NoMovieException extends RuntimeException {
	public NoMovieException() {
		super();
	}

	public NoMovieException(String message) {
		super(message);
	}

	public NoMovieException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoMovieException(Throwable cause) {
		super(cause);
	}
}
