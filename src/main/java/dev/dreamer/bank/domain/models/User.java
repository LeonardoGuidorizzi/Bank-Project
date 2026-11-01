package dev.dreamer.bank.domain.models;

import dev.dreamer.bank.domain.enums.UserRole;

import java.util.Locale;

public class User {
    public static final UserRole DEFAULT_ROLE = UserRole.CUSTOMER;

    private final String id;
    private String username;
    private String email;
    private String passwordHash;
    private UserRole role;

    public User(
            String id,
            String username,
            String email,
            String passwordHash
    ) {
        this(id, username, email, passwordHash, DEFAULT_ROLE);
    }

    public User(
            String id,
            String username,
            String email,
            String passwordHash,
            UserRole role
    ) {
        this.id = id;
        this.username = username != null ? username.trim() : null;
        this.email = normalizeEmail(email);
        this.passwordHash = passwordHash;
        this.role = role != null ? role : DEFAULT_ROLE;
    }

    public static String normalizeEmail(String email) {
        return email != null ? email.trim().toLowerCase(Locale.ROOT) : null;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getName() {
        return getUsername();
    }

    public void setName(String name) {
        setUsername(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalizeEmail(email);
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
