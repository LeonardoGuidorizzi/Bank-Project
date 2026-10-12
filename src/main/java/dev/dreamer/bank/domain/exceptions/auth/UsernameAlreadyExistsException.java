package dev.dreamer.bank.domain.exceptions.auth;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super("Nome de usuário já cadastrado");
    }
}
