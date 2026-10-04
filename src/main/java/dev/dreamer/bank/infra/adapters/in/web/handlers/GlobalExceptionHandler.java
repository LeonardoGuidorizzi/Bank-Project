package dev.dreamer.bank.infra.adapters.in.web.handlers;

import dev.dreamer.bank.domain.exceptions.EmailAlreadyExistsException;
import dev.dreamer.bank.domain.exceptions.InvalidCredentialsException;
import dev.dreamer.bank.domain.exceptions.InvalidUserDataException;
import dev.dreamer.bank.domain.exceptions.UserNotFoundException;
import dev.dreamer.bank.domain.exceptions.UsernameAlreadyExistsException;
import dev.dreamer.bank.infra.adapters.in.web.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Dados inválidos", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Corpo da requisição ausente ou malformado", null);
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserData(InvalidUserDataException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler({EmailAlreadyExistsException.class, UsernameAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    /**
     * Covers the race where two sign-ups with the same e-mail/username pass the existence
     * check at the same time and the database unique constraint rejects the second one.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "E-mail ou nome de usuário já cadastrado", null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, InvalidCredentialsException.MESSAGE, null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, Map<String, String> fields) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, fields));
    }
}
