package core.application.reviews.exceptions;

public class InvalidReviewEditException extends RuntimeException {

    public InvalidReviewEditException() {
    }

    public InvalidReviewEditException(String message) {
        super(message);
    }

    public InvalidReviewEditException(Throwable cause) {
        super(cause);
    }

    public InvalidReviewEditException(String message, Throwable cause) {
        super(message, cause);
    }
}
