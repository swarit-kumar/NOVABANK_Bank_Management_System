package com.bank.model;

import java.time.LocalDate;

public class LoanPayment {
    private String paymentNumber;
    private LocalDate paymentDate;
    private double paidAmount;
    private double leftAmount;
    private String loanNumber;

    // Constructors
    public LoanPayment() {}

    public LoanPayment(String paymentNumber, LocalDate paymentDate, double paidAmount,
                       double leftAmount, String loanNumber) {
        this.paymentNumber = paymentNumber;
        this.paymentDate = paymentDate;
        this.paidAmount = paidAmount;
        this.leftAmount = leftAmount;
        this.loanNumber = loanNumber;
    }

    // Getters and Setters
    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
    public double getLeftAmount() { return leftAmount; }
    public void setLeftAmount(double leftAmount) { this.leftAmount = leftAmount; }
    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }

    @Override
    public String toString() {
        return "LoanPayment{" +
                "paymentNumber='" + paymentNumber + '\'' +
                ", paymentDate=" + paymentDate +
                ", paidAmount=" + paidAmount +
                ", leftAmount=" + leftAmount +
                ", loanNumber='" + loanNumber + '\'' +
                '}';
    }
}