package com.bank.model;

public class Loan {
    private String loanNumber;
    private double amount;
    private String loanType;
    private String loanStatus;

    // Constructors
    public Loan() {}

    public Loan(String loanNumber, double amount, String loanType, String loanStatus) {
        this.loanNumber = loanNumber;
        this.amount = amount;
        this.loanType = loanType;
        this.loanStatus = loanStatus;
    }

    // Getters and Setters
    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public String getLoanStatus() { return loanStatus; }
    public void setLoanStatus(String loanStatus) { this.loanStatus = loanStatus; }

    @Override
    public String toString() {
        return "Loan{" +
                "loanNumber='" + loanNumber + '\'' +
                ", amount=" + amount +
                ", loanType='" + loanType + '\'' +
                ", loanStatus='" + loanStatus + '\'' +
                '}';
    }
}