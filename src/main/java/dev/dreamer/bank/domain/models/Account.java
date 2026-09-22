package dev.dreamer.bank.domain.models;

import dev.dreamer.bank.domain.enums.AccountStatus;
import dev.dreamer.bank.domain.enums.AccountType;

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

    public static Account create( String accountNumber, UUID userId, BigDecimal balance, AccountType accountType, AccountStatus accountStatus) {
        return null;
    }


}
