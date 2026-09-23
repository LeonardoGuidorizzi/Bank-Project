package dev.dreamer.bank.domain.exceptions.account;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
