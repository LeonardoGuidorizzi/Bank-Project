package dev.dreamer.bank.controller;

import dev.dreamer.bank.dto.CustomerLoanRequest;
import dev.dreamer.bank.dto.CustomerLoanResponse;
import dev.dreamer.bank.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("loan")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/customer-loans")
    public ResponseEntity<CustomerLoanResponse> customerLoans(@RequestBody @Valid CustomerLoanRequest request){

        var loanResponse = loanService.loanAvailability(request);
        return ResponseEntity.ok(loanResponse);
    }
}
