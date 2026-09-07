package dev.dreamer.bank.dto;

import dev.dreamer.bank.domain.Customer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

public record CustomerLoanRequest(
       @NotNull @Min(18) Integer age,
       @CPF @NotBlank String cpf,
       @NotBlank String name,
       @NotNull @Min(1000) Double income,
       @NotBlank String location
) {
    public Customer toCustomer () {
        return new Customer(
                age,
                cpf,
                name,
                income,
                location
        );
    }
}
