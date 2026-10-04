package dev.dreamer.bank.infra.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Nome de usuário é obrigatório")
        @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$",
                message = "Nome de usuário deve conter apenas letras, números, '.', '_' ou '-'")
        String username,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
        String password
) {}
