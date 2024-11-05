package core.application.reviews.exceptions;

public class FailedToUploadImageException extends RuntimeException {

    public FailedToUploadImageException() {
    }

    public FailedToUploadImageException(String message) {
        super(message);
    }

    public FailedToUploadImageException(Throwable cause) {
        super(cause);
    }

    public FailedToUploadImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
