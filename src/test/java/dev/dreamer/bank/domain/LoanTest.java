package dev.dreamer.bank.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanTest {

    @Mock
    private Customer customer;

    @InjectMocks
    private Loan loan;
    @Nested
    class IsPersonalLoanAvailable {
        @Test
        void ShouldBeAvailableWhenIncomeIsEqualOrLowerThan3k(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(true);
            assertTrue(loan.isPersonalLoanAvailable());
        }

        @Test
        void ShouldBeAvailableWhenIncomeIsBetween3kand5kAgeIsLowerThan30LocantionIsEqualtoSp(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(false);
            when(customer.isIncomeBetween(3000.0,5000.0)).thenReturn(true);
            when(customer.isAgeLowerThan(30)).thenReturn(true);
            when(customer.locationIsEqual("SP")).thenReturn(true);
            assertTrue(loan.isPersonalLoanAvailable());
        }

        @Test
        void ShouldNotBeAvailableWhenIncomeIsNotEqualOrLowerThan3k(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(false);
            assertFalse(loan.isPersonalLoanAvailable());
        }

    }
    @Nested
    class IsSecuredLoanAvailable {
        @Test
        void ShouldBeAvailableWhenIncomeIsEqualOrLowerThan3k(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(true);
            assertTrue(loan.isSecuredLoanAvailable());
        }

        @Test
        void ShouldBeAvailableWhenIncomeIsBetween3kand5kAgeIsLowerThan30LocantionIsEqualtoSp(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(false);
            when(customer.isIncomeBetween(3000.0,5000.0)).thenReturn(true);
            when(customer.isAgeLowerThan(30)).thenReturn(true);
            when(customer.locationIsEqual("SP")).thenReturn(true);
            assertTrue(loan.isSecuredLoanAvailable());
        }

        @Test
        void ShouldNotBeAvailableWhenIncomeIsNotEqualOrLowerThan3k(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(false);
            assertFalse(loan.isSecuredLoanAvailable());
        }

    }


    @Nested
    class IsConsigmentLoanAvailable {
        @Test
        void ShouldBeAvailableWhenIncomeIsEqualOrLowerThan3k(){
            when(customer.isIncomeEqualOrGreaterThan(5000.0)).thenReturn(true);
            assertTrue(loan.isConsigmentLoanAvailable());
        }

        @Test
        void ShouldNotBeAvailableWhenIncomeIsNotEqualOrLowerThan3k(){
            when(customer.isIncomeEqualOrGreaterThan(5000.0)).thenReturn(false);
            assertFalse(loan.isConsigmentLoanAvailable());
        }

    }

    @Nested
    class ShouldPersonalInterestRate{
        @Test
        void ShouldInterestRateBe4(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(true);
            assertEquals(4, loan.getPersonalInterestRate());
        }

        @Test
        void ShouldThrowExceptionInterestRateBe4(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(false);
            assertThrows(LoanNotAvailableException.class, () -> loan.getPersonalInterestRate());
        }

    }

    @Nested
    class ShouldSecuredInterestRate{
        @Test
        void ShouldInterestRateBe3(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(true);
            assertEquals(3, loan.getSecuredInterestRate());
        }

        @Test
        void ShouldThrowExceptionWhenInterestRateIsNot3(){
            when(customer.isIncomeEqualOrLowerThan(3000.0)).thenReturn(false);
            assertThrows(LoanNotAvailableException.class, () -> loan.getSecuredInterestRate());
        }

    }

    @Nested
    class ShouldConsigmentInterestRate{
        @Test
        void ShouldInterestRateBe2(){
            when(customer.isIncomeEqualOrGreaterThan(5000.0)).thenReturn(true);
            assertEquals(2, loan.getConsigmentInterestRate());
        }

        @Test
        void ShouldThrowExceptionWhenInterestRateIsNot2(){
            when(customer.isIncomeEqualOrGreaterThan(5000.0)).thenReturn(false);
            assertThrows(LoanNotAvailableException.class, () -> loan.getConsigmentInterestRate());
        }

    }



}
