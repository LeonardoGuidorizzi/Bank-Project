package dev.dreamer.bank.infra.adapters.in.web.dto;

import dev.dreamer.bank.domain.models.User;

/**
 * Public view of a user. Never exposes the password hash.
 */
public record UserResponse(
        String id,
        String username,
        String email,
        String role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
