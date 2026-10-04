package dev.dreamer.bank.infra.adapters.in.web.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn
) {}
