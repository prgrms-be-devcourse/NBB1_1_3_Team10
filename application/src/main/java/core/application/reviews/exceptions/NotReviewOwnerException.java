package core.application.reviews.exceptions;

public class NotReviewOwnerException extends RuntimeException {

    public NotReviewOwnerException() {
    }

    public NotReviewOwnerException(String message) {
        super(message);
    }

    public NotReviewOwnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotReviewOwnerException(Throwable cause) {
        super(cause);
    }
}
