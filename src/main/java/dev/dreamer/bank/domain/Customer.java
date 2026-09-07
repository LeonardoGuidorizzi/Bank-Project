package dev.dreamer.bank.domain;

public class Customer {
    private Integer age;
    private String cpf;
    private String name;
    private Double income;
    private String location;

    public Customer(Integer age, String cpf, String name, Double income, String location) {
        this.age = age;
        this.cpf = cpf;
        this.name = name;
        this.income = income;
        this.location = location;
    }

    public boolean isIncomeEqualOrLowerThan(double v) {
        return this.income <= v;
    }
    public boolean isIncomeEqualOrGreaterThan(double v) {
        return this.income >= v;
    }

    public boolean isIncomeBetween(double v1, double v2) {
        return this.income >= v1 && this.income <= v2;
    }

    public boolean  isAgeLowerThan(int v) {
        return this.age < v;
    }


    public boolean locationIsEqual(String location) {
        return this.location.equals(location);
    }

    public Integer getAge() {
        return age;
    }

    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public Double getIncome() {
        return income;
    }

    public String getLocation() {
        return location;
    }
}
