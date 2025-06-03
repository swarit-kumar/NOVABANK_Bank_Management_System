package com.bank.service;

import com.bank.MainFX;
import com.bank.dao.LoanDAO;
import com.bank.model.Loan;
import com.bank.model.LoanPayment;
import com.bank.model.LoanPolicy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoanService {
    private LoanDAO loanDAO;

    public LoanService() {
        this.loanDAO = new LoanDAO();
    }

    // Get all available loan policies
    public List<LoanPolicy> getLoanPolicies() {
        return loanDAO.getAllLoanPolicies();
    }

    // Get client's loans
    public List<Loan> getClientLoans(int clientId) {
        return loanDAO.getLoansByClientId(clientId);
    }

    // Apply for a new loan
    public boolean applyForLoan(int clientId, int staffId, double amount, String loanType) {
        // Validate input
        if (amount <= 0 || loanType == null || loanType.isEmpty()) {
            return false;
        }

        // Check if loan type exists and amount is within limit
        LoanPolicy policy = getLoanPolicy(loanType);
        if (policy == null || amount > policy.getMaxAmount()) {
            return false;
        }

        // Generate loan number
        String loanNumber = "LN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        return loanDAO.applyForLoan(loanNumber, clientId, staffId, amount, loanType, "Active");
    }

    // Get loan payments with ownership validation
    public List<LoanPayment> getLoanPaymentHistory(String loanNumber) {
        if (MainFX.currentClient == null) {
            System.out.println("No client logged in.");
            return new ArrayList<>();
        }

        System.out.println("Fetching loans for clientId: " + MainFX.currentClient.getClientId());
        List<Loan> clientLoans = loanDAO.getLoansByClientId(MainFX.currentClient.getClientId());
        System.out.println("Client loans found: " + clientLoans.size());
        clientLoans.forEach(loan -> System.out.println("Loan: " + loan.getLoanNumber()));

        boolean isAuthorized = clientLoans.stream()
                .anyMatch(loan -> loan.getLoanNumber().equalsIgnoreCase(loanNumber));
        System.out.println("Loan number " + loanNumber + " authorized: " + isAuthorized);

        if (!isAuthorized) {
            System.out.println("Client not authorized for loan: " + loanNumber);
            return new ArrayList<>();
        }

        List<LoanPayment> payments = loanDAO.getLoanPayments(loanNumber);
        System.out.println("Payments found for loan " + loanNumber + ": " + payments.size());
        return payments;
    }

    // Make a loan payment
    public boolean makeLoanPayment(String loanNumber, double paymentAmount) {
        if (paymentAmount <= 0 || MainFX.currentClient == null) {
            return false;
        }

        // Verify loan ownership before processing payment
        List<Loan> clientLoans = loanDAO.getLoansByClientId(MainFX.currentClient.getClientId());
        boolean isAuthorized = clientLoans.stream()
                .anyMatch(loan -> loan.getLoanNumber().equals(loanNumber));

        if (!isAuthorized) {
            return false; // Prevent payment if loan is not owned by the client
        }

        // Get the loan details
        List<LoanPayment> payments = loanDAO.getLoanPayments(loanNumber);
        if (payments.isEmpty()) {
            return false;
        }

        // Get the latest payment to check remaining amount
        LoanPayment latestPayment = payments.get(0);
        double remainingAmount = latestPayment.getLeftAmount();

        if (paymentAmount > remainingAmount) {
            paymentAmount = remainingAmount; // Don't allow overpayment
        }

        double newRemainingAmount = remainingAmount - paymentAmount;

        // Generate payment number
        String paymentNumber = "P" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return loanDAO.makeLoanPayment(paymentNumber, LocalDate.now(), paymentAmount, newRemainingAmount, loanNumber);
    }

    // Helper method to get specific loan policy
    private LoanPolicy getLoanPolicy(String loanType) {
        List<LoanPolicy> policies = loanDAO.getAllLoanPolicies();
        for (LoanPolicy policy : policies) {
            if (policy.getLoanType().equals(loanType)) {
                return policy;
            }
        }
        return null;
    }
}