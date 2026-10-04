package dev.dreamer.bank.domain.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("E-mail já cadastrado");
    }
}
