package dev.dreamer.bank.application.services;

import dev.dreamer.bank.domain.exceptions.InvalidCredentialsException;
import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.in.LoginUseCase;
import dev.dreamer.bank.domain.ports.out.HashPort;
import dev.dreamer.bank.domain.ports.out.TokenPort;
import dev.dreamer.bank.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoginService implements LoginUseCase {
    public static final String TOKEN_TYPE = "Bearer";

    private final UserRepositoryPort userRepository;
    private final HashPort hashPort;
    private final TokenPort tokenPort;

    /**
     * Hash compared against when the user does not exist, so both failure paths take
     * roughly the same time and response timing does not reveal which e-mails are registered.
     */
    private final String dummyHash;

    public LoginService(UserRepositoryPort userRepository, HashPort hashPort, TokenPort tokenPort) {
        this.userRepository = userRepository;
        this.hashPort = hashPort;
        this.tokenPort = tokenPort;
        this.dummyHash = hashPort.hash("dummy-password-for-timing-protection");
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginCommand command) {
        if (command == null || isBlank(command.login()) || isBlank(command.password())) {
            throw new InvalidCredentialsException();
        }

        Optional<User> user = findByLogin(command.login());

        String hashToCompare = user.map(User::getPasswordHash).orElse(dummyHash);
        boolean passwordMatches = hashPort.matches(command.password(), hashToCompare);

        if (user.isEmpty() || !passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String token = tokenPort.generateToken(user.get());
        return new LoginResult(token, TOKEN_TYPE, tokenPort.getExpirationSeconds());
    }

    private Optional<User> findByLogin(String login) {
        String trimmed = login.trim();
        if (trimmed.contains("@")) {
            return userRepository.findByEmail(User.normalizeEmail(trimmed));
        }
        return userRepository.findByUsername(trimmed);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
