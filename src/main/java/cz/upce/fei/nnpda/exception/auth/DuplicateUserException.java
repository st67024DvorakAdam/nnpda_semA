package cz.upce.fei.nnpda.exception.auth;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }
}

