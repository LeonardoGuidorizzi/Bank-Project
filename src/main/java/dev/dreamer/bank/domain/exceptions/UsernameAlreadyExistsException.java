package dev.dreamer.bank.domain.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super("Nome de usuário já cadastrado");
    }
}
