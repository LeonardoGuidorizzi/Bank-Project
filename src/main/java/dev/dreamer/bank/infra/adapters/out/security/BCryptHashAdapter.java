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
        return encoder.matches(rawString, hashedString);
    }
}
