package dev.dreamer.bank.domain.exceptions.auth.account;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
