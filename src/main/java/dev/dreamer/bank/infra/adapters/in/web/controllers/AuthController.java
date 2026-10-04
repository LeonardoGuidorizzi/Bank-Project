package dev.dreamer.bank.infra.adapters.in.web.controllers;

import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.in.LoginUseCase;
import dev.dreamer.bank.domain.ports.in.LoginUseCase.LoginCommand;
import dev.dreamer.bank.domain.ports.in.LoginUseCase.LoginResult;
import dev.dreamer.bank.domain.ports.in.SignUpUseCase;
import dev.dreamer.bank.domain.ports.in.SignUpUseCase.SignUpCommand;
import dev.dreamer.bank.infra.adapters.in.web.dto.LoginRequest;
import dev.dreamer.bank.infra.adapters.in.web.dto.LoginResponse;
import dev.dreamer.bank.infra.adapters.in.web.dto.SignUpRequest;
import dev.dreamer.bank.infra.adapters.in.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final SignUpUseCase signUpUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(SignUpUseCase signUpUseCase, LoginUseCase loginUseCase) {
        this.signUpUseCase = signUpUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody SignUpRequest request) {
        User user = signUpUseCase.signUp(
                new SignUpCommand(request.username(), request.email(), request.password()));
        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = loginUseCase.login(new LoginCommand(request.login(), request.password()));
        return new LoginResponse(result.token(), result.tokenType(), result.expiresIn());
    }
}
