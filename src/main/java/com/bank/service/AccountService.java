package com.bank.service;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import com.bank.model.Account;
import com.bank.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AccountService {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // Get all accounts for a client
    public List<Account> getClientAccounts(int clientId) {
        return accountDAO.getAccountsByClientId(clientId);
    }

    // Get account details
    public Account getAccountDetails(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }

    // Deposit money
    public boolean deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            return false;
        }

        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account == null || !account.getStatus().equals("Active")) {
            return false;
        }

        double newBalance = account.getBalance() + amount;
        boolean balanceUpdated = accountDAO.updateAccountBalance(accountNumber, newBalance);

        if (balanceUpdated) {
            // Record transaction
            Transaction transaction = new Transaction();
            transaction.setTransactionId(generateTransactionId());
            transaction.setTransactionType("Deposit");
            transaction.setAmount(amount);
            transaction.setDateTime(LocalDateTime.now());
            transaction.setSourceAccount(accountNumber);
            transaction.setDestinationAccount(null);

            return transactionDAO.createTransaction(transaction);
        }

        return false;
    }

    // Withdraw money
    public boolean withdraw(String accountNumber, double amount, int clientId) {
        if (amount <= 0) {
            return false;
        }
        
        Account account = accountDAO.getAccountByNumber(accountNumber);
        
        // ✅ Verify ownership and other conditions
        if (account == null || account.getClientId() != clientId ||
                !account.getStatus().equals("Active") || account.getBalance() < amount) {
            return false;
        }
        
        double newBalance = account.getBalance() - amount;
        boolean balanceUpdated = accountDAO.updateAccountBalance(accountNumber, newBalance);
        
        if (balanceUpdated) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(generateTransactionId());
            transaction.setTransactionType("Withdrawal");
            transaction.setAmount(amount);
            transaction.setDateTime(LocalDateTime.now());
            transaction.setSourceAccount(accountNumber);
            transaction.setDestinationAccount(null);
            
            return transactionDAO.createTransaction(transaction);
        }
        
        return false;
    }
    
    
    // Transfer money between accounts
    public boolean transfer(String sourceAccountNumber, String destinationAccountNumber, double amount, int clientId) {
        if (amount <= 0 || sourceAccountNumber.equals(destinationAccountNumber)) {
            return false;
        }
        
        Account sourceAccount = accountDAO.getAccountByNumber(sourceAccountNumber);
        Account destinationAccount = accountDAO.getAccountByNumber(destinationAccountNumber);
        
        // ✅ Validate ownership and account statuses
        if (sourceAccount == null || destinationAccount == null ||
                sourceAccount.getClientId() != clientId ||  // Check that user owns source
                !sourceAccount.getStatus().equals("Active") || !destinationAccount.getStatus().equals("Active") ||
                sourceAccount.getBalance() < amount) {
            return false;
        }
        
        double newSourceBalance = sourceAccount.getBalance() - amount;
        double newDestinationBalance = destinationAccount.getBalance() + amount;
        
        boolean sourceUpdated = accountDAO.updateAccountBalance(sourceAccountNumber, newSourceBalance);
        boolean destinationUpdated = accountDAO.updateAccountBalance(destinationAccountNumber, newDestinationBalance);
        
        if (sourceUpdated && destinationUpdated) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(generateTransactionId());
            transaction.setTransactionType("Transfer");
            transaction.setAmount(amount);
            transaction.setDateTime(LocalDateTime.now());
            transaction.setSourceAccount(sourceAccountNumber);
            transaction.setDestinationAccount(destinationAccountNumber);
            
            return transactionDAO.createTransaction(transaction);
        } else {
            // Rollback balances
            if (sourceUpdated) {
                accountDAO.updateAccountBalance(sourceAccountNumber, sourceAccount.getBalance());
            }
            if (destinationUpdated) {
                accountDAO.updateAccountBalance(destinationAccountNumber, destinationAccount.getBalance());
            }
            return false;
        }
    }
    
    
    // Get transaction history for an account
    public List<Transaction> getAccountTransactions(String accountNumber) {
        return transactionDAO.getTransactionsByAccount(accountNumber);
    }

    // Get transaction history for a client (all accounts)
    public List<Transaction> getClientTransactions(int clientId) {
        return transactionDAO.getTransactionsByClient(clientId);
    }

    // Helper method to generate transaction ID
    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
