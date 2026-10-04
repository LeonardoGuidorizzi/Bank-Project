package dev.dreamer.bank.application.services;

import dev.dreamer.bank.domain.exceptions.InvalidCredentialsException;
import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.in.LoginUseCase.LoginCommand;
import dev.dreamer.bank.domain.ports.in.LoginUseCase.LoginResult;
import dev.dreamer.bank.domain.ports.out.HashPort;
import dev.dreamer.bank.domain.ports.out.TokenPort;
import dev.dreamer.bank.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    private static final String DUMMY_HASH = "dummy-hash";
    private static final String REAL_HASH = "real-hash";

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private HashPort hashPort;

    @Mock
    private TokenPort tokenPort;

    private LoginService loginService;
    private User user;

    @BeforeEach
    void setUp() {
        when(hashPort.hash(anyString())).thenReturn(DUMMY_HASH);
        loginService = new LoginService(userRepository, hashPort, tokenPort);
        user = new User("user-id", "john", "john@example.com", REAL_HASH);
    }

    @Test
    @DisplayName("Should return a token when credentials are valid")
    void shouldReturnTokenForValidCredentials() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(hashPort.matches("secret123", REAL_HASH)).thenReturn(true);
        when(tokenPort.generateToken(user)).thenReturn("jwt-token");
        when(tokenPort.getExpirationSeconds()).thenReturn(3600L);

        LoginResult result = loginService.login(new LoginCommand("john@example.com", "secret123"));

        assertEquals("jwt-token", result.token());
        assertEquals("Bearer", result.tokenType());
        assertEquals(3600L, result.expiresIn());
    }

    @Test
    @DisplayName("Should log in by username")
    void shouldLoginByUsername() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(hashPort.matches("secret123", REAL_HASH)).thenReturn(true);
        when(tokenPort.generateToken(user)).thenReturn("jwt-token");

        LoginResult result = loginService.login(new LoginCommand(" john ", "secret123"));

        assertEquals("jwt-token", result.token());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should log in with e-mail in a different case")
    void shouldLoginWithEmailInDifferentCase() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(hashPort.matches("secret123", REAL_HASH)).thenReturn(true);
        when(tokenPort.generateToken(user)).thenReturn("jwt-token");

        LoginResult result = loginService.login(new LoginCommand("  JOHN@Example.com ", "secret123"));

        assertEquals("jwt-token", result.token());
    }

    @Test
    @DisplayName("Wrong password and unknown user must fail with the exact same generic error")
    void shouldNotRevealWhetherUserOrPasswordIsWrong() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());
        when(hashPort.matches(anyString(), anyString())).thenReturn(false);

        InvalidCredentialsException wrongPassword = assertThrows(InvalidCredentialsException.class,
                () -> loginService.login(new LoginCommand("john@example.com", "wrong-pass")));
        InvalidCredentialsException unknownUser = assertThrows(InvalidCredentialsException.class,
                () -> loginService.login(new LoginCommand("ghost@example.com", "secret123")));

        assertEquals(wrongPassword.getMessage(), unknownUser.getMessage());
        assertEquals(InvalidCredentialsException.MESSAGE, unknownUser.getMessage());
        verify(tokenPort, never()).generateToken(any());
    }

    @Test
    @DisplayName("Unknown user still runs a hash comparison (timing protection)")
    void shouldCompareAgainstDummyHashWhenUserDoesNotExist() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> loginService.login(new LoginCommand("ghost@example.com", "secret123")));

        verify(hashPort).matches(eq("secret123"), eq(DUMMY_HASH));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, secret123",
            "'', secret123",
            "john@example.com, null",
            "john@example.com, '   '"
    }, nullValues = "null")
    @DisplayName("Should reject null or blank credentials with the generic error")
    void shouldRejectBlankCredentials(String login, String password) {
        assertThrows(InvalidCredentialsException.class,
                () -> loginService.login(new LoginCommand(login, password)));
        verifyNoInteractions(userRepository, tokenPort);
    }
}
