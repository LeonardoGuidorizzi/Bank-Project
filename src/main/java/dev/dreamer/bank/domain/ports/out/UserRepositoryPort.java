package dev.dreamer.bank.domain.ports.out;

import dev.dreamer.bank.domain.models.User;

import java.util.Optional;

public interface UserRepositoryPort {
    boolean existsByEmail(String email);
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
}
