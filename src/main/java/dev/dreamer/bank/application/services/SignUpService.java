package dev.dreamer.bank.application.services;

import dev.dreamer.bank.domain.exceptions.EmailAlreadyExistsException;
import dev.dreamer.bank.domain.exceptions.InvalidUserDataException;
import dev.dreamer.bank.domain.exceptions.UsernameAlreadyExistsException;
import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.in.SignUpUseCase;
import dev.dreamer.bank.domain.ports.out.HashPort;
import dev.dreamer.bank.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class SignUpService implements SignUpUseCase {
    public static final int MIN_PASSWORD_LENGTH = 6;
    /** BCrypt only uses the first 72 bytes of the input. */
    public static final int MAX_PASSWORD_BYTES = 72;

    private final UserRepositoryPort userRepository;
    private final HashPort hashPort;

    public SignUpService(UserRepositoryPort userRepository, HashPort hashPort) {
        this.userRepository = userRepository;
        this.hashPort = hashPort;
    }

    @Override
    @Transactional
    public User signUp(SignUpCommand command) {
        validate(command);

        String username = command.username().trim();
        String email = User.normalizeEmail(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }

        String passwordHash = hashPort.hash(command.password());
        User user = new User(null, username, email, passwordHash, User.DEFAULT_ROLE);

        return userRepository.save(user);
    }

    private void validate(SignUpCommand command) {
        if (command == null) {
            throw new InvalidUserDataException("Dados de cadastro são obrigatórios");
        }
        if (isBlank(command.username())) {
            throw new InvalidUserDataException("Nome de usuário é obrigatório");
        }
        if (isBlank(command.email())) {
            throw new InvalidUserDataException("E-mail é obrigatório");
        }
        if (isBlank(command.password())) {
            throw new InvalidUserDataException("Senha é obrigatória");
        }
        if (command.password().length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidUserDataException(
                    "A senha deve ter pelo menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
        if (command.password().getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new InvalidUserDataException("A senha é muito longa");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
