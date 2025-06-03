package com.bank.model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String transactionType;
    private double amount;
    private LocalDateTime dateTime;
    private String sourceAccount;
    private String destinationAccount;

    // Constructors
    public Transaction() {}

    public Transaction(String transactionId, String transactionType, double amount,
                       LocalDateTime dateTime, String sourceAccount, String destinationAccount) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.dateTime = dateTime;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(String sourceAccount) { this.sourceAccount = sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public void setDestinationAccount(String destinationAccount) { this.destinationAccount = destinationAccount; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", dateTime=" + dateTime +
                ", sourceAccount='" + sourceAccount + '\'' +
                ", destinationAccount='" + (destinationAccount != null ? destinationAccount : "N/A") + '\'' +
                '}';
    }
}