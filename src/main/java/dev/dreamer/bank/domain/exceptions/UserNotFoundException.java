package dev.dreamer.bank.domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuário não encontrado");
    }
}
