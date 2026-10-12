package dev.dreamer.bank.infra.adapters.out.security;

import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.out.TokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenAdapter implements TokenPort {
    static final String ROLE_CLAIM = "role";
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey key;
    private final long expirationMillis;
    private final Clock clock;
    private final JwtParser parser;

    @Autowired
    public JwtTokenAdapter(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-millis}") long expirationMillis
    ) {
        this(secret, expirationMillis, Clock.systemUTC());
    }

    public JwtTokenAdapter(String secret, long expirationMillis, Clock clock) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "security.jwt.secret must have at least " + MIN_SECRET_BYTES + " bytes");
        }
        if (expirationMillis <= 0) {
            throw new IllegalStateException("security.jwt.expiration-millis must be positive");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
        this.clock = clock;
        this.parser = Jwts.parser()
                .verifyWith(key)
                .clock(() -> Date.from(clock.instant()))
                .build();
    }

    @Override
    public String generateToken(User user) {
        Instant now = clock.instant();
        Instant expiresAt = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .subject(user.getId())
                .claim(ROLE_CLAIM, user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public Optional<TokenClaims> parseToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            // parseSignedClaims rejects unsigned tokens, bad signatures and expired tokens
            Claims claims = parser.parseSignedClaims(token).getPayload();
            if (claims.getSubject() == null || claims.getExpiration() == null) {
                return Optional.empty();
            }
            return Optional.of(new TokenClaims(claims.getSubject(), claims.get(ROLE_CLAIM, String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public long getExpirationSeconds() {
        return expirationMillis / 1000;
    }
}
