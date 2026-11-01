package dev.dreamer.bank.domain.ports.out;

public interface HashPort {
    String hash(String rawString);
    boolean matches(String rawString, String hashedString);
}
