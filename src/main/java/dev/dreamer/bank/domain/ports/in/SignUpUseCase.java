package dev.dreamer.bank.domain.ports.in;

import dev.dreamer.bank.domain.models.User;

public interface SignUpUseCase {
    record SignUpCommand(
            String username,
            String email,
            String password
    ) {}

    User signUp(SignUpCommand command);
}
