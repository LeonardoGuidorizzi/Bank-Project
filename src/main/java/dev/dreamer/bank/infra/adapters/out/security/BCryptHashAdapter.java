package dev.dreamer.bank.infra.adapters.out.security;

import dev.dreamer.bank.domain.ports.out.HashPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptHashAdapter implements HashPort {
    private final BCryptPasswordEncoder encoder;

    public BCryptHashAdapter() {
       this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public String hash(String rawString) {
        return encoder.encode(rawString);
    }

    @Override
    public boolean matches(String rawString, String hashedString) {
        if (rawString == null || hashedString == null) {
            return false;
        }
        try {
            return encoder.matches(rawString, hashedString);
        } catch (IllegalArgumentException e) {
            // BCrypt rejects inputs longer than 72 bytes; such a password can never match
            return false;
        }
    }
}
