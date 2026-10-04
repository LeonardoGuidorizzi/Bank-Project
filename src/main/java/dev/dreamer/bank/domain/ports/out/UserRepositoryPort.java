package dev.dreamer.bank.domain.ports.out;

import dev.dreamer.bank.domain.models.User;

import java.util.Optional;

public interface UserRepositoryPort {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(String id);
}
