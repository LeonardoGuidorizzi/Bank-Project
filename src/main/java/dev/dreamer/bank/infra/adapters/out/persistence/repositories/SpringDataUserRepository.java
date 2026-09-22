package dev.dreamer.bank.infra.adapters.out.persistence.repositories;

import dev.dreamer.bank.infra.adapters.out.persistence.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    boolean existsByEmail(String email);
    Optional<UserJpaEntity> findByEmail(String email);
}
