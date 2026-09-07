package dev.dreamer.bank.service;

import dev.dreamer.bank.domain.Loan;
import dev.dreamer.bank.domain.LoanType;
import dev.dreamer.bank.dto.CustomerLoanRequest;
import dev.dreamer.bank.dto.CustomerLoanResponse;
import dev.dreamer.bank.dto.LoanResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    public CustomerLoanResponse loanAvailability (CustomerLoanRequest customerLoanRequest) {
        var customer = customerLoanRequest.toCustomer();
        var loan = new Loan(customer);
        List<LoanResponse> loans = new ArrayList<>();
        if (loan.isPersonalLoanAvailable()) {
            loans.add(new LoanResponse(LoanType.PERSONAL, loan.getPersonalInterestRate()));
        }
        if(loan.isConsigmentLoanAvailable()){
            loans.add(new LoanResponse(LoanType.CONSIGNMENT, loan.getConsigmentInterestRate()));
        }
        if(loan.isSecuredLoanAvailable()){
            loans.add(new LoanResponse(LoanType.GUARANTEED, loan.getSecuredInterestRate()));
        }
        return new CustomerLoanResponse (customer.getName(), loans);
    }
}
