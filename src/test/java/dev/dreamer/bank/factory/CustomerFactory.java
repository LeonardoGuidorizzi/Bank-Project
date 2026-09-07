package dev.dreamer.bank.factory;

import dev.dreamer.bank.domain.Customer;

public class CustomerFactory {

    public static Customer build() {
        return new Customer(18, "123.456.789-00", "Name", 5000.00, "SP" );
    }

    public static Customer build(Integer age) {
        return new Customer(age, "123.456.789-00", "Name", 5000.00, "SP" );
    }
    public static Customer build(String location) {
        return new Customer(18, "123.456.789-00", "Name", 5000.00, "SP" );
    }

    public static Customer build(Double income) {
        return new Customer(18, "123.456.789-00", "Name", income, "SP" );
    }
}
//it's a way to understand junit