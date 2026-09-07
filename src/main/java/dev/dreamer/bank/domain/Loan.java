package dev.dreamer.bank.domain;

public class Loan {
    Customer customer;
    public Loan(Customer customer) {
        this.customer = customer;
    }

    public boolean isPersonalLoanAvailable() {
        return basicLoanAvailable();

    }

    public double getPersonalInterestRate() {
        if(isPersonalLoanAvailable()){
            return 4.0;
        }
        throw new LoanNotAvailableException();
    }

    public boolean isConsigmentLoanAvailable() {
        return customer.isIncomeEqualOrGreaterThan(5000.00);
    }

    public double getConsigmentInterestRate() {
        if(isConsigmentLoanAvailable()){
            return 2.0;
        }
        throw new LoanNotAvailableException();
    }


    public boolean isSecuredLoanAvailable() {
        return basicLoanAvailable();

    }

    private boolean basicLoanAvailable() {
        if (customer.isIncomeEqualOrLowerThan(3000.00)) {
            return true;
        }
        ;
        return customer.isIncomeBetween(3000.00, 5000.00)
                && customer.isAgeLowerThan(30)
                && customer.locationIsEqual("SP");
    }

    public double getSecuredInterestRate() {
        if(isSecuredLoanAvailable()){
            return 3.0;
        }
        throw new LoanNotAvailableException();
    }





}
