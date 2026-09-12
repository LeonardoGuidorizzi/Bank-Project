CREATE TABLE users (
                       id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username        VARCHAR(50)  NOT NULL UNIQUE,
                       email           VARCHAR(150) NOT NULL UNIQUE,
                       password_hash   VARCHAR(255) NOT NULL,
                       role            VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER',
                       created_at      TIMESTAMP    NOT NULL DEFAULT now(),

                       CONSTRAINT chk_users_role CHECK (role IN ('CUSTOMER', 'ADMIN'))
);

CREATE TABLE accounts (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          account_number  VARCHAR(20)    NOT NULL UNIQUE,
                          user_id         UUID           NOT NULL REFERENCES users(id),
                          balance         NUMERIC(19,2)  NOT NULL DEFAULT 0,
                          account_type    VARCHAR(20)    NOT NULL DEFAULT 'CHECKING',
                          status          VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
                          created_at      TIMESTAMP      NOT NULL DEFAULT now(),

                          CONSTRAINT chk_accounts_balance_non_negative CHECK (balance >= 0),
                          CONSTRAINT chk_accounts_type CHECK (account_type IN ('CHECKING', 'SAVINGS')),
                          CONSTRAINT chk_accounts_status CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED'))
);

CREATE INDEX idx_accounts_user_id ON accounts(user_id);