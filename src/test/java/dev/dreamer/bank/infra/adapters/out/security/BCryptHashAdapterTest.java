package dev.dreamer.bank.infra.adapters.out.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptHashAdapterTest {

    private BCryptHashAdapter hashAdapter;

    @BeforeEach
    void setUp() {
        hashAdapter = new BCryptHashAdapter();
    }

    @Test
    @DisplayName("Should generate a valid BCrypt hash for a raw string")
    void shouldGenerateValidHash() {
        String rawPassword = "mySecretPassword123";

        String hashedPassword = hashAdapter.hash(rawPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$"));
    }

    @Test
    @DisplayName("Should return true when raw string matches hashed string")
    void shouldReturnTrueWhenRawStringMatchesHashedString() {
        String rawPassword = "mySecretPassword123";
        String hashedPassword = hashAdapter.hash(rawPassword);

        boolean matches = hashAdapter.matches(rawPassword, hashedPassword);

        assertTrue(matches);
    }

    @Test
    @DisplayName("Should return false when raw string does not match hashed string")
    void shouldReturnFalseWhenRawStringDoesNotMatchHashedString() {
        String rawPassword = "mySecretPassword123";
        String wrongPassword = "wrongPassword456";
        String hashedPassword = hashAdapter.hash(rawPassword);

        boolean matches = hashAdapter.matches(wrongPassword, hashedPassword);

        assertFalse(matches);
    }
}
