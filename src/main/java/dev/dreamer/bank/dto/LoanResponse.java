package dev.dreamer.bank.dto;

import dev.dreamer.bank.domain.LoanType;

public record LoanResponse(
        LoanType type,
        Double interestRate
) {

}
