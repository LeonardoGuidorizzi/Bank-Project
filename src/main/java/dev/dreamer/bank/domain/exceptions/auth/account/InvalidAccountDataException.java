package dev.dreamer.bank.domain.exceptions.auth.account;

public class InvalidAccountDataException extends RuntimeException {
    public InvalidAccountDataException(String message) {
        super(message);
    }
}
