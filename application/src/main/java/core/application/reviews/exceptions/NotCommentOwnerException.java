package core.application.reviews.exceptions;

public class NotCommentOwnerException extends RuntimeException {

    public NotCommentOwnerException() {
    }

    public NotCommentOwnerException(String message) {
        super(message);
    }

    public NotCommentOwnerException(Throwable cause) {
        super(cause);
    }

    public NotCommentOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
