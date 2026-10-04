package dev.dreamer.bank.domain.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public static final String MESSAGE = "Credenciais inválidas";

    public InvalidCredentialsException() {
        super(MESSAGE);
    }
}
