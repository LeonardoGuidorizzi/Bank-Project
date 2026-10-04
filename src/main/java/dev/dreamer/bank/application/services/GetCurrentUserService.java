package dev.dreamer.bank.application.services;

import dev.dreamer.bank.domain.exceptions.UserNotFoundException;
import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.in.GetCurrentUserUseCase;
import dev.dreamer.bank.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCurrentUserService implements GetCurrentUserUseCase {
    private final UserRepositoryPort userRepository;

    public GetCurrentUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(String userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}
