package core.application.security.exception;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super();
    }

    public RefreshTokenNotFoundException(String message) {
        super(message);
    }

    public RefreshTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenNotFoundException(Throwable cause) {
        super(cause);
    }
}
