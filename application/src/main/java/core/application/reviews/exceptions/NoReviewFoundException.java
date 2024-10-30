package core.application.reviews.exceptions;

/**
 * 영화 후기 포스팅이 발견되지 않았을 때 던져지는 예외
 */
public class NoReviewFoundException extends RuntimeException {
    public NoReviewFoundException() {
    }

    public NoReviewFoundException(Long reviewId) {
        super("No review found with id [" + reviewId + "]");
    }

    public NoReviewFoundException(String message) {
        super(message);
    }

    public NoReviewFoundException(Throwable cause) {
        super(cause);
    }

    public NoReviewFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
