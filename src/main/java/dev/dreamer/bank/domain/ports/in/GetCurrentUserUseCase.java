package dev.dreamer.bank.domain.ports.in;

import dev.dreamer.bank.domain.models.User;

public interface GetCurrentUserUseCase {
    User getById(String userId);
}
