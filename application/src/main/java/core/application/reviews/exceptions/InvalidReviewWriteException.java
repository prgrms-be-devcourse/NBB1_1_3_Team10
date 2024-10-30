package core.application.reviews.exceptions;

public class InvalidReviewWriteException extends RuntimeException {

    public InvalidReviewWriteException() {
    }

    public InvalidReviewWriteException(String message) {
        super(message);
    }

    public InvalidReviewWriteException(Throwable cause) {
        super(cause);
    }

    public InvalidReviewWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
