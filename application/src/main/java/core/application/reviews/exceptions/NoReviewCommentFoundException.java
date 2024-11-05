package core.application.reviews.exceptions;

/**
 * 특정 영화 후기 포스팅 댓글이 발견되지 않았을 때 던져지는 예외
 */
public class NoReviewCommentFoundException extends RuntimeException {
    public NoReviewCommentFoundException() {
    }

    public NoReviewCommentFoundException(Long reviewCommentId) {
        super("No review comment found with id [" + reviewCommentId + "]");
    }

    public NoReviewCommentFoundException(String message) {
        super(message);
    }

    public NoReviewCommentFoundException(Throwable cause) {
        super(cause);
    }

    public NoReviewCommentFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
