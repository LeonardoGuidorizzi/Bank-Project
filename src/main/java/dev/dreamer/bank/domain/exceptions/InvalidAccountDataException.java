package dev.dreamer.bank.domain.exceptions;

public class InvalidAccountDataException extends RuntimeException {
    public InvalidAccountDataException(String message) {
        super(message);
    }
}
