package dev.dreamer.bank.application.services;

import dev.dreamer.bank.domain.enums.UserRole;
import dev.dreamer.bank.domain.exceptions.EmailAlreadyExistsException;
import dev.dreamer.bank.domain.exceptions.InvalidUserDataException;
import dev.dreamer.bank.domain.exceptions.UsernameAlreadyExistsException;
import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.in.SignUpUseCase.SignUpCommand;
import dev.dreamer.bank.domain.ports.out.HashPort;
import dev.dreamer.bank.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private HashPort hashPort;

    private SignUpService signUpService;

    @BeforeEach
    void setUp() {
        signUpService = new SignUpService(userRepository, hashPort);
    }

    private void stubSaveReturnsWithId() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return new User("generated-id", u.getUsername(), u.getEmail(), u.getPasswordHash(), u.getRole());
        });
    }

    @Test
    @DisplayName("Should create user with default CUSTOMER role and hashed password")
    void shouldCreateUserWithDefaultRoleAndHashedPassword() {
        when(hashPort.hash("secret123")).thenReturn("hashed-secret");
        stubSaveReturnsWithId();

        User created = signUpService.signUp(new SignUpCommand("john", "john@example.com", "secret123"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals(UserRole.CUSTOMER, saved.getRole());
        assertEquals("hashed-secret", saved.getPasswordHash());
        assertNotEquals("secret123", saved.getPasswordHash());
        assertEquals("generated-id", created.getId());
        assertEquals(UserRole.CUSTOMER, created.getRole());
    }

    @Test
    @DisplayName("Should reject sign-up when e-mail already exists, with a clear message")
    void shouldRejectDuplicateEmail() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class,
                () -> signUpService.signUp(new SignUpCommand("john", "john@example.com", "secret123")));

        assertEquals("E-mail já cadastrado", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(hashPort, never()).hash(anyString());
    }

    @Test
    @DisplayName("Should treat e-mails with different case as duplicates")
    void shouldRejectDuplicateEmailWithDifferentCase() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> signUpService.signUp(new SignUpCommand("john", "  John@Example.COM ", "secret123")));
    }

    @Test
    @DisplayName("Should reject sign-up when username already exists")
    void shouldRejectDuplicateUsername() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class,
                () -> signUpService.signUp(new SignUpCommand("john", "john@example.com", "secret123")));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should store e-mail trimmed and in lower case")
    void shouldNormalizeEmail() {
        when(hashPort.hash(anyString())).thenReturn("hashed");
        stubSaveReturnsWithId();

        User created = signUpService.signUp(new SignUpCommand("  john ", "  John.Doe@Example.COM ", "secret123"));

        verify(userRepository).existsByEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", created.getEmail());
        assertEquals("john", created.getUsername());
    }

    @Test
    @DisplayName("Should reject password shorter than the minimum length")
    void shouldRejectShortPassword() {
        InvalidUserDataException ex = assertThrows(InvalidUserDataException.class,
                () -> signUpService.signUp(new SignUpCommand("john", "john@example.com", "12345")));

        assertTrue(ex.getMessage().contains(String.valueOf(SignUpService.MIN_PASSWORD_LENGTH)));
        verifyNoInteractions(userRepository, hashPort);
    }

    @Test
    @DisplayName("Should reject password longer than 72 bytes (BCrypt limit)")
    void shouldRejectTooLongPassword() {
        assertThrows(InvalidUserDataException.class,
                () -> signUpService.signUp(new SignUpCommand("john", "john@example.com", "a".repeat(73))));
        verifyNoInteractions(userRepository, hashPort);
    }

    static Stream<Arguments> nullOrBlankFields() {
        return Stream.of(
                Arguments.of(null, "john@example.com", "secret123"),
                Arguments.of("   ", "john@example.com", "secret123"),
                Arguments.of("john", null, "secret123"),
                Arguments.of("john", "", "secret123"),
                Arguments.of("john", "john@example.com", null),
                Arguments.of("john", "john@example.com", "      ")
        );
    }

    @ParameterizedTest
    @MethodSource("nullOrBlankFields")
    @DisplayName("Should reject null or blank fields")
    void shouldRejectNullOrBlankFields(String username, String email, String password) {
        assertThrows(InvalidUserDataException.class,
                () -> signUpService.signUp(new SignUpCommand(username, email, password)));
        verifyNoInteractions(userRepository, hashPort);
    }

    @Test
    @DisplayName("Should reject null command")
    void shouldRejectNullCommand() {
        assertThrows(InvalidUserDataException.class, () -> signUpService.signUp(null));
    }
}
