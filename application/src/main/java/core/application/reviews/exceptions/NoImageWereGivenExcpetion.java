package core.application.reviews.exceptions;

public class NoImageWereGivenExcpetion extends RuntimeException {

    public NoImageWereGivenExcpetion() {
    }

    public NoImageWereGivenExcpetion(String message) {
        super(message);
    }

    public NoImageWereGivenExcpetion(Throwable cause) {
        super(cause);
    }

    public NoImageWereGivenExcpetion(String message, Throwable cause) {
        super(message, cause);
    }

}
