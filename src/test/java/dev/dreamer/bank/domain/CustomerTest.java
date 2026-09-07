package dev.dreamer.bank.domain;

import dev.dreamer.bank.factory.CustomerFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CustomerTest {

    @Nested
    class isIncomeEqualOrLowerThan{
        @Test
        void shouldReturnTrueWhenIncomeIsEqual() {
            var customer = CustomerFactory.build(5000.00);
            assertTrue(customer.isIncomeEqualOrLowerThan(5000.00));
        }
        @Test
        void shouldReturnTrueWhenIncomeIsLower() {
            var customer = CustomerFactory.build(3000.00);
            assertTrue(customer.isIncomeEqualOrLowerThan(5000.00));
        }

        @Test
        void shouldReturnFalseWhenIncomeIsGreater() {
            var customer = CustomerFactory.build(8000.00);
            assertFalse(customer.isIncomeEqualOrLowerThan(5000.00));
        }
    }


    @Nested
    class isIncomeEqualOrGreaterThan{
        @Test
        void shouldReturnTrueWhenIncomeIsEqual() {
            var customer = CustomerFactory.build(5000.00);
            assertTrue(customer.isIncomeEqualOrGreaterThan(5000.00));
        }
        @Test
        void shouldReturnTrueWhenIncomeIsGreaterThan() {
            var customer = CustomerFactory.build(6000.00);
            assertTrue(customer.isIncomeEqualOrGreaterThan(5000.00));
        }

        @Test
        void shouldReturnFalseWhenIncomeIsLower() {
            var customer = CustomerFactory.build(5000.00);
            assertFalse(customer.isIncomeEqualOrGreaterThan(8000.00));
        }
    }
    @Nested
    class isIncomeBetween{
        @Test
        void shouldReturnTrueWhenIncomeIsBetween() {
            var customer = CustomerFactory.build(5000.00);
            assertTrue(customer.isIncomeBetween(3000.00, 7000.00));
        }

        @Test
        void shouldReturnFalseWhenIncomeIsNotBetween() {
            var customer = CustomerFactory.build(6000.00);
            assertFalse(customer.isIncomeBetween(3000.00, 5000.00));
        }
    }

    @Nested
    class isAgeLowerThan{
        @Test
        void shouldReturnTrueWhenAgeIsLowerThan() {
            var customer = CustomerFactory.build(16);
            assertTrue(customer.isAgeLowerThan(18));
        }

        @Test
        void shouldReturnFalseWhenAgeIsNotLowerThan() {
            var customer = CustomerFactory.build(30);
            assertFalse(customer.isAgeLowerThan(18));
        }

        @Test
        void shouldReturnFalseWhenAgeIsEqual() {
            var customer = CustomerFactory.build(18);
            assertFalse(customer.isAgeLowerThan(18));
        }


    }

    @Nested
    class isLocationEqual{
        @Test
        void shouldReturnTrueWhenLocationIsEqual() {
            var customer = CustomerFactory.build("SP");
            assertTrue(customer.locationIsEqual("SP"));
        }

        @Test
        void shouldReturnFalseWhenLocationIsNotEqual() {
            var customer = CustomerFactory.build("SP");
            assertFalse(customer.locationIsEqual("RJ"));
        }

    }



    @Test
    void isIncomeEqualOrGreaterThan() {
    }

    @Test
    void isIncomeBetween() {
    }

    @Test
    void isAgeLowerThan() {
    }

    @Test
    void locationIsEqual() {
    }
}