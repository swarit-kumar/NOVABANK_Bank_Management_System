package com.bank.model;

public class LoanPolicy {
    private String loanType;
    private double interestRate;
    private double maxAmount;

    // Constructors
    public LoanPolicy() {}

    public LoanPolicy(String loanType, double interestRate, double maxAmount) {
        this.loanType = loanType;
        this.interestRate = interestRate;
        this.maxAmount = maxAmount;
    }

    // Getters and Setters
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(double maxAmount) { this.maxAmount = maxAmount; }

    @Override
    public String toString() {
        return "LoanPolicy{" +
                "loanType='" + loanType + '\'' +
                ", interestRate=" + interestRate +
                ", maxAmount=" + maxAmount +
                '}';
    }
}