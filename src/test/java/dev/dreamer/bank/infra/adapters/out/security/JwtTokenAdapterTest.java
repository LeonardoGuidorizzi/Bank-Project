package dev.dreamer.bank.infra.adapters.out.security;

import dev.dreamer.bank.domain.enums.UserRole;
import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.out.TokenPort.TokenClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenAdapterTest {
    private static final String SECRET = "test-secret-key-for-jwt-signing-must-be-32-bytes-long";
    private static final long ONE_HOUR = Duration.ofHours(1).toMillis();

    private JwtTokenAdapter adapter;
    private User user;

    @BeforeEach
    void setUp() {
        adapter = new JwtTokenAdapter(SECRET, ONE_HOUR);
        user = new User("3f1c2a9e-0000-0000-0000-000000000001", "john", "john@example.com", "hash", UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("Should generate a token whose claims can be read back")
    void shouldGenerateAndParseToken() {
        String token = adapter.generateToken(user);

        Optional<TokenClaims> claims = adapter.parseToken(token);

        assertTrue(claims.isPresent());
        assertEquals(user.getId(), claims.get().userId());
        assertEquals("CUSTOMER", claims.get().role());
        assertEquals(3600, adapter.getExpirationSeconds());
    }

    @Test
    @DisplayName("Should reject an expired token")
    void shouldRejectExpiredToken() {
        Clock twoHoursAgo = Clock.fixed(Instant.now().minus(Duration.ofHours(2)), ZoneOffset.UTC);
        String expiredToken = new JwtTokenAdapter(SECRET, ONE_HOUR, twoHoursAgo).generateToken(user);

        assertTrue(adapter.parseToken(expiredToken).isEmpty());
    }

    @Test
    @DisplayName("Should reject a token with a tampered payload")
    void shouldRejectTamperedPayload() {
        String[] parts = adapter.generateToken(user).split("\\.");
        String forgedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"someone-else\",\"role\":\"ADMIN\",\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));
        String tampered = parts[0] + "." + forgedPayload + "." + parts[2];

        assertTrue(adapter.parseToken(tampered).isEmpty());
    }

    @Test
    @DisplayName("Should reject a token signed with a different secret")
    void shouldRejectTokenSignedWithAnotherSecret() {
        String foreignToken = new JwtTokenAdapter("another-secret-key-that-is-also-32-bytes-long!!", ONE_HOUR)
                .generateToken(user);

        assertTrue(adapter.parseToken(foreignToken).isEmpty());
    }

    @Test
    @DisplayName("Should reject an unsigned token (alg=none)")
    void shouldRejectUnsignedToken() {
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        String header = enc.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
        String payload = enc.encodeToString(("{\"sub\":\"" + user.getId() + "\",\"exp\":9999999999}")
                .getBytes(StandardCharsets.UTF_8));

        assertTrue(adapter.parseToken(header + "." + payload + ".").isEmpty());
    }

    @Test
    @DisplayName("Should reject null, blank and malformed tokens")
    void shouldRejectMalformedTokens() {
        assertTrue(adapter.parseToken(null).isEmpty());
        assertTrue(adapter.parseToken("").isEmpty());
        assertTrue(adapter.parseToken("   ").isEmpty());
        assertTrue(adapter.parseToken("not-a-jwt").isEmpty());
        assertTrue(adapter.parseToken("a.b.c").isEmpty());
    }

    @Test
    @DisplayName("Should fail fast when the secret is missing or too short")
    void shouldRejectWeakSecret() {
        assertThrows(IllegalStateException.class, () -> new JwtTokenAdapter(null, ONE_HOUR));
        assertThrows(IllegalStateException.class, () -> new JwtTokenAdapter("short-secret", ONE_HOUR));
    }

    @Test
    @DisplayName("Should fail fast when the expiration is not positive")
    void shouldRejectNonPositiveExpiration() {
        assertThrows(IllegalStateException.class, () -> new JwtTokenAdapter(SECRET, 0));
    }
}
