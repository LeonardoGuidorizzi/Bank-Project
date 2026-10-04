package dev.dreamer.bank.domain.ports.out;

import dev.dreamer.bank.domain.models.User;

import java.util.Optional;

public interface TokenPort {
    record TokenClaims(String userId, String role) {}

    String generateToken(User user);

    Optional<TokenClaims> parseToken(String token);

    long getExpirationSeconds();
}
