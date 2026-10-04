package dev.dreamer.bank.infra.adapters.in.web.controllers;

import dev.dreamer.bank.domain.ports.in.GetCurrentUserUseCase;
import dev.dreamer.bank.infra.adapters.in.web.dto.UserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public UserController(GetCurrentUserUseCase getCurrentUserUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    /**
     * Protected route: requires a valid token. The principal is the user id from the token subject.
     */
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal String userId) {
        return UserResponse.from(getCurrentUserUseCase.getById(userId));
    }
}
