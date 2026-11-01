package dev.dreamer.bank.domain.exceptions.auth;

public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
