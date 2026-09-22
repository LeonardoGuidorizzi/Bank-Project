package dev.dreamer.bank.infra.adapters.out.persistence;

import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.out.UserRepositoryPort;
import dev.dreamer.bank.infra.adapters.out.persistence.entities.UserJpaEntity;
import dev.dreamer.bank.infra.adapters.out.persistence.repositories.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    public UserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = toEntity(user);
        UserJpaEntity savedEntity = springDataUserRepository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        try {
            UUID uuid = UUID.fromString(id);
            return springDataUserRepository.findById(uuid).map(this::toDomain);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private UserJpaEntity toEntity(User user) {
        UUID id = user.getId() != null && !user.getId().isBlank() ? UUID.fromString(user.getId()) : null;

        return new UserJpaEntity(
                id,
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                "CUSTOMER",
                null,
                null
        );
    }

    private User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId() != null ? entity.getId().toString() : null,
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash()
        );
    }
}
