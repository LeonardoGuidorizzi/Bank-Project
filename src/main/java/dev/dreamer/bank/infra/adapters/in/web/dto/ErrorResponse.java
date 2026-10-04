package dev.dreamer.bank.infra.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> fields,
        Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, null, Instant.now());
    }

    public static ErrorResponse of(int status, String error, String message, Map<String, String> fields) {
        return new ErrorResponse(status, error, message, fields, Instant.now());
    }
}
