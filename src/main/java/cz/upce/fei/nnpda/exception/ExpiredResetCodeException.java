package cz.upce.fei.nnpda.exception;

public class ExpiredResetCodeException extends RuntimeException {
    public ExpiredResetCodeException(String message) {
        super(message);
    }
}
