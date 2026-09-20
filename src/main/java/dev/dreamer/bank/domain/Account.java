package dev.dreamer.bank.domain;

import dev.dreamer.bank.domain.enums.AccountStatus;
import dev.dreamer.bank.domain.enums.AccountType;
import dev.dreamer.bank.domain.exceptions.InvalidAccountDataException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Account {

    UUID id;
    String accountNumber;
    UUID userId;
    BigDecimal balance;
    AccountType accountType;
    AccountStatus status;
    LocalDateTime creationDate;

    public Account() {
    }

    public static Account create( String accountNumber, UUID userId, BigDecimal balance, AccountType accountType, AccountStatus accountStatus,  LocalDateTime creationDate ) {
        validate(accountNumber, userId, balance, accountType, accountStatus, creationDate);

       return null;
    }


    private static void validate(String accountNumber, UUID userId, BigDecimal balance, AccountType accountType, AccountStatus accountStatus,   LocalDateTime creationDate  ) {
        if (accountNumber == null|| accountNumber.isBlank()){
            throw new InvalidAccountDataException("Account number cannot be null or empty");
        }
        if (accountType == null){
            throw new InvalidAccountDataException("Account type cannot be null");
        }
        if (userId == null){
            throw new InvalidAccountDataException("Account user cannot be null");
        }
        if (balance == null){
            throw new InvalidAccountDataException("Balance cannot be null");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0){
            throw new InvalidAccountDataException("Balance cannot be less than zero");
        }
        if (accountStatus == null){
            throw new InvalidAccountDataException("Account status cannot be null");
        }
        if (creationDate == null){
            throw new InvalidAccountDataException("Account creation date cannot be null");
        }


    }



    public UUID getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
