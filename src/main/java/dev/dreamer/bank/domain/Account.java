package dev.dreamer.bank.domain;

import dev.dreamer.bank.domain.enums.AccountStatus;
import dev.dreamer.bank.domain.enums.AccountType;
import dev.dreamer.bank.domain.exceptions.InvalidAccountDataException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Account {

    UUID id;
    String accountNumber;
    UUID userId;
    BigDecimal balance;
    AccountType accountType;
    AccountStatus status;
    LocalDateTime creationDate;


    private Account(UUID id, String accountNumber, UUID userId, BigDecimal balance, AccountType accountType, AccountStatus accountstatus, LocalDateTime creationDate) {
        validate(accountNumber, userId, balance, accountType, accountstatus, creationDate);
        this.id = id;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
        this.accountType = accountType;
        this.status = accountstatus;
        this.creationDate = creationDate;
    }

    public static Account create(UUID userId, AccountType accountType) {
      UUID id = UUID.randomUUID();
      String accountNumber = generateAccountNumber();
      BigDecimal balance = BigDecimal.ZERO;
      AccountStatus status = AccountStatus.ACTIVE;
      LocalDateTime creationDate = LocalDateTime.now();
       return new Account(id, accountNumber, userId, balance, accountType, status, creationDate);
    }



    public static Account restore(UUID id, String accountNumber, UUID userId, BigDecimal balance, AccountType accountType, AccountStatus accountStatus, LocalDateTime creationDate){

        return new Account(id, accountNumber, userId, balance, accountType, accountStatus, creationDate);
    }

    private static String generateAccountNumber() {
        int numbers = ThreadLocalRandom.current().nextInt(10000, 100000);
        String accountNumber = String.valueOf(numbers);
        int sum = 0;
        boolean alternate = true;
        for (int i  = accountNumber.length()-1;  i >= 0; i--) { // -> se tiver 6 ele vai começar do 5
            int digit = accountNumber.charAt(i) - '0'; // -> converte para inteiro
            if (alternate){
                digit *= 2; // digit = digit * 2;
            }
            if(digit > 9){
                digit -=9;
            }
            sum += digit;
            alternate = !alternate;

        }
        int checkNumber = (10 - (sum % 10)) %10; // se for 43 o resto vai ser 3 e esse 3 vai subtrair o 10
        return   accountNumber + checkNumber;
    }

    //todo: withdraw and deposit method


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
