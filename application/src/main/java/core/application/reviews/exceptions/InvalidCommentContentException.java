package core.application.reviews.exceptions;

public class InvalidCommentContentException extends RuntimeException {

    public InvalidCommentContentException() {
    }

    public InvalidCommentContentException(String message) {
        super(message);
    }

    public InvalidCommentContentException(Throwable cause) {
        super(cause);
    }

    public InvalidCommentContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
