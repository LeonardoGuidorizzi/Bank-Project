package dev.dreamer.bank.domain.exceptions.auth;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("E-mail já cadastrado");
    }
}
