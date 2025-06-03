CREATE DATABASE IF NOT EXISTS BANK;
USE BANK;

CREATE TABLE IF NOT EXISTS ADMIN (
    AdminID     INT auto_increment PRIMARY KEY,
    first_name  VARCHAR(50) NOT NULL,
    last_name   VARCHAR(50) NOT NULL,
    Email       VARCHAR(100) UNIQUE NOT NULL,
    Password    VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS STAFF (
    StaffID     INT auto_increment PRIMARY KEY,
    first_name  VARCHAR(50) NOT NULL,
    last_name   VARCHAR(50) NOT NULL,
    Email       VARCHAR(100) NOT NULL,
    Password    VARCHAR(100) NOT NULL,
    Role        VARCHAR(50) CHECK (Role IN ('Manager', 'Clerk', 'Teller'))
);

CREATE TABLE IF NOT EXISTS CLIENT (
    ClientID     INT auto_increment PRIMARY KEY,
    first_name   VARCHAR(50) NOT NULL,
    last_name    VARCHAR(50) NOT NULL,
    Email        VARCHAR(100) NOT NULL,
    Password     VARCHAR(100) NOT NULL,
    PhoneNumber  VARCHAR(15) NOT NULL,
    Street       VARCHAR(100),
    City         VARCHAR(50),
    State        VARCHAR(50),
    PinCode      VARCHAR(6),
    DOB          DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS ACCOUNT (
    AccountNumber   VARCHAR(20) PRIMARY KEY,  
    ClientID        INT,                 
    AccountType     VARCHAR(20) CHECK (AccountType IN ('Savings', 'Current', 'Fixed Deposit')),
    Balance         FLOAT CHECK (Balance >= 0),
    Status          VARCHAR(20) CHECK (Status IN ('Active', 'Closed', 'Suspended')),
    CreatedAt       DATETIME DEFAULT CURRENT_TIMESTAMP, 
    CreatedBy       INT, 
    FOREIGN KEY (ClientID) REFERENCES CLIENT(ClientID) ON DELETE SET NULL,
    FOREIGN KEY (CreatedBy) REFERENCES STAFF(StaffID) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS TRANSACTION (
    TransactionID     VARCHAR(20) PRIMARY KEY,
    TransactionType   VARCHAR(20) CHECK (TransactionType IN ('Deposit', 'Withdrawal', 'Transfer')),
    Amount           FLOAT CHECK (Amount > 0),
    DateTime         DATETIME DEFAULT CURRENT_TIMESTAMP,
    SourceAccount    VARCHAR(20),
    DestinationAccount VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS LOAN_POLICY (
    LoanType VARCHAR(50) PRIMARY KEY,
    InterestRate FLOAT CHECK (InterestRate > 0),
    MaxAmount FLOAT CHECK (MaxAmount > 0)
);

CREATE TABLE IF NOT EXISTS LOAN (
    LoanNumber VARCHAR(20) PRIMARY KEY,
    Amount FLOAT CHECK (amount > 0),
    LoanType VARCHAR(50),
    LoanStatus VARCHAR(50) CHECK(LoanStatus IN ('Active', 'Cleared')),
    FOREIGN KEY (LoanType) REFERENCES LOAN_POLICY(LoanType) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS LOAN_HOLDER (
    LoanNumber  VARCHAR(20),
    ClientID    INT NOT NULL,
    StaffID     INT NOT NULL,
    PRIMARY KEY (LoanNumber, ClientID),
    FOREIGN KEY (LoanNumber) REFERENCES LOAN(LoanNumber) ON DELETE CASCADE,
    FOREIGN KEY (ClientID) REFERENCES CLIENT(ClientID) ON DELETE CASCADE,
    FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS LOANPAYMENT (
    PaymentNumber  VARCHAR(20),
    PaymentDate    DATE NOT NULL,
    PaidAmount     FLOAT DEFAULT 0 CHECK (paidAmount > 0),
    LeftAmount     FLOAT CHECK (leftAmount >= 0),
    LoanNumber     VARCHAR(20) NOT NULL,
    FOREIGN KEY (LoanNumber) REFERENCES LOAN(LoanNumber) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REPORTS (
	ReportID  INT auto_increment PRIMARY KEY,
    Type       VARCHAR(100) CHECK(Type in('Loan Approved', 'Accounts Created', 'Number of Transactions')),
    Timestamp  DATETIME DEFAULT CURRENT_TIMESTAMP,
    StaffID    INT,
    FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS FEEDBACK (
    ClientID   INT NOT NULL,
    Message    TEXT NOT NULL,
    Timestamp  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ClientID) REFERENCES CLIENT(ClientID) ON DELETE CASCADE
);

