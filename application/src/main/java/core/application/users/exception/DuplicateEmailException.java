package core.application.users.exception;

public class DuplicateEmailException extends RuntimeException {
	public DuplicateEmailException() {
		super();
	}

	public DuplicateEmailException(String message) {
		super(message);
	}

	public DuplicateEmailException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateEmailException(Throwable cause) {
		super(cause);
	}
}
