package dev.dreamer.bank.domain;

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

}
