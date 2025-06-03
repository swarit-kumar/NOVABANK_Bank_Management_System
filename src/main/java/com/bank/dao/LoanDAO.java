package com.bank.dao;

import com.bank.model.Loan;
import com.bank.model.LoanPayment;
import com.bank.model.LoanPolicy;
import com.bank.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    // Get all loan policies
    public List<LoanPolicy> getAllLoanPolicies() {
        List<LoanPolicy> policies = new ArrayList<>();
        String sql = "SELECT * FROM LOAN_POLICY";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                policies.add(extractLoanPolicyFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return policies;
    }

    // Get loans for a specific client
    public List<Loan> getLoansByClientId(int clientId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.* FROM LOAN l JOIN LOAN_HOLDER lh ON l.LoanNumber = lh.LoanNumber " +
                "WHERE lh.ClientID = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(extractLoanFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Get loan payments for a specific loan
    public List<LoanPayment> getLoanPayments(String loanNumber) {
        List<LoanPayment> payments = new ArrayList<>();
        String sql = "SELECT * FROM LOANPAYMENT WHERE LoanNumber = ? ORDER BY PaymentDate DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loanNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(extractLoanPaymentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // Apply for a new loan
    public boolean applyForLoan(String loanNumber, int clientId, int staffId, double amount,
                                String loanType, String loanStatus) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert into LOAN table
            String loanSql = "INSERT INTO LOAN (LoanNumber, Amount, LoanType, LoanStatus) VALUES (?, ?, ?, ?)";
            try (PreparedStatement loanStmt = conn.prepareStatement(loanSql)) {
                loanStmt.setString(1, loanNumber);
                loanStmt.setDouble(2, amount);
                loanStmt.setString(3, loanType);
                loanStmt.setString(4, loanStatus);

                int loanRows = loanStmt.executeUpdate();
                if (loanRows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Insert into LOAN_HOLDER table
            String holderSql = "INSERT INTO LOAN_HOLDER (LoanNumber, ClientID, StaffID) VALUES (?, ?, ?)";
            try (PreparedStatement holderStmt = conn.prepareStatement(holderSql)) {
                holderStmt.setString(1, loanNumber);
                holderStmt.setInt(2, clientId);
                holderStmt.setInt(3, staffId);

                int holderRows = holderStmt.executeUpdate();
                if (holderRows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Insert initial payment record (optional)
            String paymentSql = "INSERT INTO LOANPAYMENT (PaymentNumber, PaymentDate, PaidAmount, LeftAmount, LoanNumber) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement paymentStmt = conn.prepareStatement(paymentSql)) {
                String paymentNumber = "P" + loanNumber.substring(2); // Generate payment number from loan number
                paymentStmt.setString(1, paymentNumber);
                paymentStmt.setDate(2, Date.valueOf(LocalDate.now()));
                paymentStmt.setDouble(3, 0);
                paymentStmt.setDouble(4, amount);
                paymentStmt.setString(5, loanNumber);

                paymentStmt.executeUpdate(); // We don't fail if this doesn't work
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Make a loan payment
    public boolean makeLoanPayment(String paymentNumber, LocalDate paymentDate, double paidAmount,
                                   double leftAmount, String loanNumber) {
        String sql = "INSERT INTO LOANPAYMENT (PaymentNumber, PaymentDate, PaidAmount, LeftAmount, LoanNumber) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paymentNumber);
            stmt.setDate(2, Date.valueOf(paymentDate));
            stmt.setDouble(3, paidAmount);
            stmt.setDouble(4, leftAmount);
            stmt.setString(5, loanNumber);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper methods to extract objects from ResultSet
    private LoanPolicy extractLoanPolicyFromResultSet(ResultSet rs) throws SQLException {
        LoanPolicy policy = new LoanPolicy();
        policy.setLoanType(rs.getString("LoanType"));
        policy.setInterestRate(rs.getDouble("InterestRate"));
        policy.setMaxAmount(rs.getDouble("MaxAmount"));
        return policy;
    }

    private Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanNumber(rs.getString("LoanNumber"));
        loan.setAmount(rs.getDouble("Amount"));
        loan.setLoanType(rs.getString("LoanType"));
        loan.setLoanStatus(rs.getString("LoanStatus"));
        return loan;
    }

    private LoanPayment extractLoanPaymentFromResultSet(ResultSet rs) throws SQLException {
        LoanPayment payment = new LoanPayment();
        payment.setPaymentNumber(rs.getString("PaymentNumber"));
        payment.setPaymentDate(rs.getDate("PaymentDate").toLocalDate());
        payment.setPaidAmount(rs.getDouble("PaidAmount"));
        payment.setLeftAmount(rs.getDouble("LeftAmount"));
        payment.setLoanNumber(rs.getString("LoanNumber"));
        return payment;
    }

    // Get all loans
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM LOAN";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(extractLoanFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Update loan status
    public boolean updateLoanStatus(String loanNumber, String status) {
        String sql = "UPDATE LOAN SET LoanStatus = ? WHERE LoanNumber = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, loanNumber);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a loan
    public boolean deleteLoan(String loanNumber) {
        String sql = "DELETE FROM LOAN WHERE LoanNumber = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loanNumber);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}