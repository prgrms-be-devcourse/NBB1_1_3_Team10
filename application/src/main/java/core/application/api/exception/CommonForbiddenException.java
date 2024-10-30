package core.application.api.exception;

public class CommonForbiddenException extends RuntimeException {
	public CommonForbiddenException() {
		super();
	}

	public CommonForbiddenException(String message) {
		super(message);
	}

	public CommonForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommonForbiddenException(Throwable cause) {
		super(cause);
	}
}
