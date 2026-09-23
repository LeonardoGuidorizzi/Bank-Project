package dev.dreamer.bank.domain;

import dev.dreamer.bank.domain.enums.AccountStatus;
import dev.dreamer.bank.domain.enums.AccountType;
import dev.dreamer.bank.domain.exceptions.account.InvalidAccountDataException;
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

    @Test
    void restore() {
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
            UUID userId = UUID.randomUUID();
            AccountType accountType = AccountType.SAVINGS;
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
    }

    @Test
    void withdraw() {
    }
}
/*
*
assertEquals(esperado, obtido) — quando você sabe exatamente qual valor deveria sair (status, saldo, userId passado)
assertNotNull(valor) — quando você não sabe o valor exato, só que ele deveria existir (id gerado, accountNumber gerado)
assertThrows(TipoDaExcecao.class, () -> chamada()) — quando o cenário deveria falhar
assertTrue/assertFalse — reservado pra quando o próprio dado que você está checando já é um booleano por natureza
*
* */




