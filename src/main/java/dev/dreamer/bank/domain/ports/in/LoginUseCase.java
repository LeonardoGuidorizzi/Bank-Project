package dev.dreamer.bank.domain.ports.in;

public interface LoginUseCase {
    record LoginCommand(
            String login,
            String password
    ) {}

    record LoginResult(
            String token,
            String tokenType,
            long expiresIn
    ) {}

    LoginResult login(LoginCommand command);
}
