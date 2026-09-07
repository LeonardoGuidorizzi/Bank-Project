package dev.dreamer.bank.dto;

import java.util.List;

public record CustomerLoanResponse(
        String customer,
        List<LoanResponse> loanResponses
) {
}
