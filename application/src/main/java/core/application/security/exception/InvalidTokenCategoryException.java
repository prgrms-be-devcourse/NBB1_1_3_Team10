package core.application.security.exception;

public class InvalidTokenCategoryException extends RuntimeException {
    public InvalidTokenCategoryException() {
        super();
    }

    public InvalidTokenCategoryException(String message) {
        super(message);
    }

    public InvalidTokenCategoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenCategoryException(Throwable cause) {
        super(cause);
    }
}
