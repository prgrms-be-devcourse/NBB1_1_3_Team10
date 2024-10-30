package core.application.security.exception;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException() {
        super();
    }

    public UnauthorizedUserException(String message) {
        super(message);
    }

    public UnauthorizedUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedUserException(Throwable cause) {
        super(cause);
    }
}
