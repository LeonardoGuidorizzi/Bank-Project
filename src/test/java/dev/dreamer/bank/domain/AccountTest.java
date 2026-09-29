package dev.dreamer.bank.domain;

import dev.dreamer.bank.domain.enums.AccountStatus;
import dev.dreamer.bank.domain.enums.AccountType;
import dev.dreamer.bank.domain.exceptions.account.InvalidAccountDataException;
import dev.dreamer.bank.domain.exceptions.account.InvalidAmountException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AccountTest {

    @Nested
    class create {

        @Test()
        void shouldCreateAccount() {
          //Arrange
          UUID userId = UUID.randomUUID();
          AccountType accountType = AccountType.SAVINGS;
          //Act
          Account account = Account.create(userId, accountType);
          //Assert
            assertNotNull(account.getId());
          assertEquals(userId, account.getUserId());
          assertEquals( 0, BigDecimal.ZERO.compareTo(account.getBalance()) );
          assertEquals( AccountType.SAVINGS, account.getAccountType());
          assertEquals(AccountStatus.ACTIVE, account.getStatus());
          assertNotNull(account.getCreationDate());
          assertNotNull(account.getAccountNumber());
        }



        @Test()
        void shouldThrowExceptionWhenAccountUserIdIsNull() {
            assertThrows(InvalidAccountDataException.class, () ->Account.create(null, AccountType.CHECKING));
        }
        @Test()
        void shouldThrowExceptionWhenAccountTypeIdIsNull() {
            assertThrows(InvalidAccountDataException.class, () ->Account.create(UUID.randomUUID(), null));
        }

    }

    @Nested
    class validate {
        @Test()
        void shouldThrowExceptionWhenAccountNumberIsNull() {
            assertThrows(InvalidAccountDataException.class,()->{
                Account.restore(UUID.randomUUID(),
                        null,
                        UUID.randomUUID(),
                        new BigDecimal(1000),
                        AccountType.CHECKING,
                        AccountStatus.ACTIVE,
                        LocalDateTime.now());
            } );
        }

        @Test()
        void shouldThrowExceptionWhenAccountBalanceIsNull() {
            assertThrows(InvalidAccountDataException.class,()->{
                Account.restore(UUID.randomUUID(),
                        "515817",
                        UUID.randomUUID(),
                        null,
                        AccountType.CHECKING,
                        AccountStatus.ACTIVE,
                        LocalDateTime.now());
            } );
        }

        @Test()
        void shouldThrowExceptionWhenAccountBalanceIsNegative() {
            assertThrows(InvalidAccountDataException.class,()->{
                Account.restore(UUID.randomUUID(),
                        "515817",
                        UUID.randomUUID(),
                        BigDecimal.valueOf(-1),
                        AccountType.CHECKING,
                        AccountStatus.ACTIVE,
                        LocalDateTime.now());
            } );
        }

        @Test()
        void shouldThrowExceptionWhenAccountStatusIsNull() {
            assertThrows(InvalidAccountDataException.class,()->{
                Account.restore(UUID.randomUUID(),
                        "515817",
                        UUID.randomUUID(),
                        new BigDecimal(1000),
                        AccountType.CHECKING,
                        null,
                        LocalDateTime.now());
            } );
        }
        @Test()
        void shouldThrowExceptionWhenAccountCreationDateIsNull() {
            assertThrows(InvalidAccountDataException.class,()->{
                Account.restore(UUID.randomUUID(),
                        "515817",
                        UUID.randomUUID(),
                        new BigDecimal(1000),
                        AccountType.CHECKING,
                        AccountStatus.ACTIVE,
                        null);
            } );
        }
    }

    @Nested
    class restore {
        @Test()
        void shouldCreateRestoreBeConsist() {
            //Arrange
            UUID id = UUID.randomUUID();
            String accountNumber = "515817";
            UUID userId = UUID.randomUUID();
            BigDecimal balance = BigDecimal.ZERO;
            AccountType accountType = AccountType.CHECKING;
            AccountStatus accountStatus = AccountStatus.ACTIVE;
            LocalDateTime creationDate = LocalDateTime.now();
            //Act
            Account account = Account.restore(id, accountNumber, userId, balance, accountType, accountStatus, creationDate);
            //Assert
            assertEquals(id, account.getId());
            assertEquals(accountNumber, account.getAccountNumber());
            assertEquals(userId, account.getUserId());
            assertEquals(balance, account.getBalance());
            assertEquals(accountType, account.getAccountType());
            assertEquals(accountStatus, account.getStatus());
            assertEquals(creationDate, account.getCreationDate());

        }
    }

    @Nested
    class deposit {
        @Test()
        void shouldDoDeposit() {
            //Arrange
            UUID userId = UUID.randomUUID();
            AccountType accountType = AccountType.SAVINGS;
            Account account = Account.create(userId, accountType);
            BigDecimal amount = new BigDecimal(100);
            //Act
            account.deposit(amount);
            //Assert
            assertEquals(amount, account.getBalance());
        }
        @Test
        void shouldDoDepositWithBalanceAlready() {
            //Arrange

            BigDecimal amount = new BigDecimal(500);
            Account account = Account.restore(
                    UUID.randomUUID(),
                    "515817",
                    UUID.randomUUID(),
                    new BigDecimal(1000),
                    AccountType.CHECKING,
                    AccountStatus.ACTIVE,
                    LocalDateTime.now());
            //Act
            account.deposit(amount);
            //Assert
            assertEquals(new BigDecimal(1500), account.getBalance());
        }

        @Test()
        void shouldThrowExceptionWhenAmountIsNegativeOnDeposit() {
            Account account = Account.restore(
                    UUID.randomUUID(),
                    "515817",
                    UUID.randomUUID(),
                    new BigDecimal(1000),
                    AccountType.CHECKING,
                    AccountStatus.ACTIVE,
                    LocalDateTime.now());
            assertThrows(InvalidAmountException.class,()->{
                account.deposit(BigDecimal.valueOf(-1));
            });
        }

        @Test()
        void shouldThrowExceptionWhenAmountIsZeroOnDeposit() {
            Account account = Account.restore(
                    UUID.randomUUID(),
                    "515817",
                    UUID.randomUUID(),
                    new BigDecimal(1000),
                    AccountType.CHECKING,
                    AccountStatus.ACTIVE,
                    LocalDateTime.now());
            assertThrows(InvalidAmountException.class,()->{
                account.deposit(BigDecimal.ZERO);
            });
    }

    @Nested
    class withdraw {
        @Test
        void shouldDoWithdraw() {
            //Arrange
            BigDecimal amount = new BigDecimal(100);
            Account account = Account.restore(
                    UUID.randomUUID(),
                    "515817",
                    UUID.randomUUID(),
                    new BigDecimal(1000),
                    AccountType.CHECKING,
                    AccountStatus.ACTIVE,
                    LocalDateTime.now());
            //Act
            account.withdraw(amount);
            //Assert
            assertEquals(new BigDecimal(900), account.getBalance());
        }
        @Test
        void shouldWipeOutTheBalance() {
            //Arrange
            BigDecimal amount = new BigDecimal(100);
            Account account = Account.restore(
                    UUID.randomUUID(),
                    "515817",
                    UUID.randomUUID(),
                    new BigDecimal(100),
                    AccountType.CHECKING,
                    AccountStatus.ACTIVE,
                    LocalDateTime.now());
            //Act
            account.withdraw(amount);
            //Assert
            assertEquals(new BigDecimal(0), account.getBalance());
        }

        @Test
        void shouldThrowExceptionWhenAmountIsNegativeOnWithdraw() {
            //Arrange
            BigDecimal amount = new BigDecimal(-100);
            Account account = Account.restore(
                    UUID.randomUUID(),
                    "515817",
                    UUID.randomUUID(),
                    new BigDecimal(1000),
                    AccountType.CHECKING,
                    AccountStatus.ACTIVE,
                    LocalDateTime.now());

            //Act
            assertThrows(InvalidAmountException.class,()->{
                account.withdraw(amount);
            });
    }}

        @Test
        void shouldThrowExceptionWhenAmountIsZeroOnWithdraw() {
            //Arrange
            BigDecimal amount = BigDecimal.ZERO;
            Account account = Account.restore(
                    UUID.randomUUID(),
                    "515817",
                    UUID.randomUUID(),
                    new BigDecimal(1000),
                    AccountType.CHECKING,
                    AccountStatus.ACTIVE,
                    LocalDateTime.now());

            //Act
            assertThrows(InvalidAmountException.class,()->{
                account.withdraw(amount);
            });
        }}
}
/*
*
assertEquals(esperado, obtido) — quando você sabe exatamente qual valor deveria sair (status, saldo, userId passado)
assertNotNull(valor) — quando você não sabe o valor exato, só que ele deveria existir (id gerado, accountNumber gerado)
assertThrows(TipoDaExcecao.class, () -> chamada()) — quando o cenário deveria falhar
assertTrue/assertFalse — reservado pra quando o próprio dado que você está checando já é um booleano por natureza
*
* */




