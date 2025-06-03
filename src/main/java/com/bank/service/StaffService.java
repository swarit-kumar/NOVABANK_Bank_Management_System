package com.bank.service;

import com.bank.dao.*;
import com.bank.model.*;
import com.bank.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class StaffService {
    private Staff staff;
    private AccountDAO accountDAO;
    private ClientDAO clientDAO;
    private TransactionDAO transactionDAO;
    private LoanDAO loanDAO;
    private FeedbackDAO feedbackDAO;

    public StaffService(Staff staff) {
        this.staff = staff;
        this.accountDAO = new AccountDAO();
        this.clientDAO = new ClientDAO();
        this.transactionDAO = new TransactionDAO();
        this.loanDAO = new LoanDAO();
        this.feedbackDAO = new FeedbackDAO();
    }

    // Account Operations
    public boolean createAccount(int clientId, String accountType, double initialBalance) {
        String accountNumber = "ACC" + (int) (Math.random() * 1000000); // Simple unique ID
        String sql = "INSERT INTO ACCOUNT (AccountNumber, ClientID, AccountType, Balance, Status, CreatedBy) VALUES (?, ?, ?, ?, 'Active', ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setInt(2, clientId);
            stmt.setString(3, accountType);
            stmt.setDouble(4, initialBalance);
            stmt.setInt(5, staff.getStaffId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
            return false;
        }
    }

    public boolean closeAccount(String accountNumber) {
        String sql = "UPDATE ACCOUNT SET Status = 'Closed' WHERE AccountNumber = ? AND Status != 'Closed'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error closing account: " + e.getMessage());
            return false;
        }
    }

    public boolean suspendAccount(String accountNumber) {
        String sql = "UPDATE ACCOUNT SET Status = 'Suspended' WHERE AccountNumber = ? AND Status = 'Active'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error suspending account: " + e.getMessage());
            return false;
        }
    }

    public boolean deposit(String accountNumber, double amount) {
        String updateSql = "UPDATE ACCOUNT SET Balance = Balance + ? WHERE AccountNumber = ? AND Status = 'Active'";
        String insertSql = "INSERT INTO TRANSACTION (TransactionID, TransactionType, Amount, DateTime, SourceAccount) VALUES (?, 'Deposit', ?, ?, ?)";
        String transactionId = "TXN" + (int) (Math.random() * 1000000);
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                updateStmt.setDouble(1, amount);
                updateStmt.setString(2, accountNumber);
                int rows = updateStmt.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
                insertStmt.setString(1, transactionId);
                insertStmt.setDouble(2, amount);
                insertStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                insertStmt.setString(4, accountNumber);
                insertStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error processing deposit: " + e.getMessage());
            return false;
        }
    }

    public boolean withdraw(String accountNumber, double amount) {
        String checkSql = "SELECT Balance FROM ACCOUNT WHERE AccountNumber = ? AND Status = 'Active'";
        String updateSql = "UPDATE ACCOUNT SET Balance = Balance - ? WHERE AccountNumber = ? AND Status = 'Active' AND Balance >= ?";
        String insertSql = "INSERT INTO TRANSACTION (TransactionID, TransactionType, Amount, DateTime, SourceAccount) VALUES (?, 'Withdrawal', ?, ?, ?)";
        String transactionId = "TXN" + (int) (Math.random() * 1000000);
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                checkStmt.setString(1, accountNumber);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next() || rs.getDouble("Balance") < amount) {
                    conn.rollback();
                    return false;
                }
                updateStmt.setDouble(1, amount);
                updateStmt.setString(2, accountNumber);
                updateStmt.setDouble(3, amount);
                int rows = updateStmt.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
                insertStmt.setString(1, transactionId);
                insertStmt.setDouble(2, amount);
                insertStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                insertStmt.setString(4, accountNumber);
                insertStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error processing withdrawal: " + e.getMessage());
            return false;
        }
    }

    public boolean transfer(String fromAccount, String toAccount, double amount) {
        String checkSql = "SELECT Balance FROM ACCOUNT WHERE AccountNumber = ? AND Status = 'Active'";
        String updateFromSql = "UPDATE ACCOUNT SET Balance = Balance - ? WHERE AccountNumber = ? AND Status = 'Active' AND Balance >= ?";
        String updateToSql = "UPDATE ACCOUNT SET Balance = Balance + ? WHERE AccountNumber = ? AND Status = 'Active'";
        String insertSql = "INSERT INTO TRANSACTION (TransactionID, TransactionType, Amount, DateTime, SourceAccount, DestinationAccount) VALUES (?, 'Transfer', ?, ?, ?, ?)";
        String transactionId = "TXN" + (int) (Math.random() * 1000000);
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement updateFromStmt = conn.prepareStatement(updateFromSql);
                 PreparedStatement updateToStmt = conn.prepareStatement(updateToSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                checkStmt.setString(1, fromAccount);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next() || rs.getDouble("Balance") < amount) {
                    conn.rollback();
                    return false;
                }
                checkStmt.setString(1, toAccount);
                rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                updateFromStmt.setDouble(1, amount);
                updateFromStmt.setString(2, fromAccount);
                updateFromStmt.setDouble(3, amount);
                int rowsFrom = updateFromStmt.executeUpdate();
                updateToStmt.setDouble(1, amount);
                updateToStmt.setString(2, toAccount);
                int rowsTo = updateToStmt.executeUpdate();
                if (rowsFrom == 0 || rowsTo == 0) {
                    conn.rollback();
                    return false;
                }
                insertStmt.setString(1, transactionId);
                insertStmt.setDouble(2, amount);
                insertStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                insertStmt.setString(4, fromAccount);
                insertStmt.setString(5, toAccount);
                insertStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error processing transfer: " + e.getMessage());
            return false;
        }
    }

    public List<Account> viewClientAccounts(int clientId) {
        return accountDAO.getAccountsByClientId(clientId);
    }

    // Loan Operations
    public boolean payLoan(String loanNumber, double amount) {
//        System.out.println("DEBUG: StaffID = " + staff.getStaffId());
        
        String getLastPaymentSQL = "SELECT LeftAmount FROM LOANPAYMENT WHERE LoanNumber = ? ORDER BY PaymentDate DESC LIMIT 1";
        String insertPaymentSQL = "INSERT INTO LOANPAYMENT (PaymentNumber, PaymentDate, PaidAmount, LeftAmount, LoanNumber) VALUES (?, ?, ?, ?, ?)";
        String clearLoanSQL = "UPDATE LOAN SET LoanStatus = 'Cleared' WHERE LoanNumber = ?";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            double leftAmount = 0;
            try (PreparedStatement getStmt = conn.prepareStatement(getLastPaymentSQL)) {
                getStmt.setString(1, loanNumber);
                ResultSet rs = getStmt.executeQuery();
                if (rs.next()) {
                    leftAmount = rs.getDouble("LeftAmount");
//                    System.out.println("DEBUG: Current LeftAmount = " + leftAmount);
                } else {
                    System.out.println("Loan not found or has no payment history.");
                    conn.rollback();
                    return false;
                }
            }
            
            // Calculate actual payment to be made
            double paidAmount = Math.min(amount, leftAmount);
            double newLeftAmount = Math.max(0, leftAmount - paidAmount);
            
            if (paidAmount <= 0) {
                System.out.println("Payment amount must be greater than 0 and not exceed remaining loan.");
                conn.rollback();
                return false;
            }
            
            String paymentId = "P00" + System.currentTimeMillis(); // Simple unique ID
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertPaymentSQL)) {
                insertStmt.setString(1, paymentId);
                insertStmt.setDate(2, today);
                insertStmt.setDouble(3, paidAmount);
                insertStmt.setDouble(4, newLeftAmount);
                insertStmt.setString(5, loanNumber);
                int inserted = insertStmt.executeUpdate();
//                System.out.println("DEBUG: Payment inserted = " + inserted);
                if (inserted == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            if (newLeftAmount == 0) {
                try (PreparedStatement clearStmt = conn.prepareStatement(clearLoanSQL)) {
                    clearStmt.setString(1, loanNumber);
                    clearStmt.executeUpdate();
                    System.out.println("DEBUG: Loan marked as Cleared.");
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Error processing loan payment: " + e.getMessage());
            return false;
        }
    }
    
    
    
    
    
    public List<LoanPayment> trackLoanPayments(String loanNumber) {
        String sql = "SELECT LP.PaymentNumber, LP.PaymentDate, LP.PaidAmount, LP.LeftAmount " +
                "FROM LOANPAYMENT LP JOIN LOAN_HOLDER LH ON LP.LoanNumber = LH.LoanNumber " +
                "WHERE LP.LoanNumber = ? AND LH.StaffID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loanNumber);
            stmt.setInt(2, staff.getStaffId());
            ResultSet rs = stmt.executeQuery();
            List<LoanPayment> payments = new java.util.ArrayList<>();
            while (rs.next()) {
                LoanPayment payment = new LoanPayment();
                payment.setPaymentNumber(rs.getString("PaymentNumber"));
                payment.setPaymentDate(rs.getDate("PaymentDate").toLocalDate());
                payment.setPaidAmount(rs.getDouble("PaidAmount"));
                payment.setLeftAmount(rs.getDouble("LeftAmount"));
                payment.setLoanNumber(loanNumber);
                payments.add(payment);
            }
            return payments;
        } catch (SQLException e) {
            System.out.println("Error tracking payments: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public boolean updateLoanStatus(String loanNumber, String status) {
        String sql = "UPDATE LOAN L JOIN LOAN_HOLDER LH ON L.LoanNumber = LH.LoanNumber " +
                "SET L.LoanStatus = ? " +
                "WHERE L.LoanNumber = ? AND LH.StaffID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, loanNumber);
            stmt.setInt(3, staff.getStaffId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating loan status: " + e.getMessage());
            return false;
        }
    }

    // Client Management
    public Client viewClientInfo(int clientId) {
        return clientDAO.getClientById(clientId);
    }

    public boolean updateClientInfo(int clientId, String email, String phone, String street, String city, String state, String pincode) {
        String sql = "UPDATE CLIENT SET Email = ?, PhoneNumber = ?, Street = ?, City = ?, State = ?, PinCode = ? WHERE ClientID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, street);
            stmt.setString(4, city);
            stmt.setString(5, state);
            stmt.setString(6, pincode);
            stmt.setInt(7, clientId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating client info: " + e.getMessage());
            return false;
        }
    }

    public List<Feedback> viewClientFeedback(int clientId) {
        return feedbackDAO.getFeedbackByClient(clientId);
    }

    // Reporting
    public void generateAccountsCreatedReport() {
        String sql = "SELECT AccountNumber, AccountType, CreatedAt, ClientID FROM ACCOUNT";
        String logSql = "INSERT INTO REPORTS (Type, Timestamp, StaffID) VALUES ('Accounts Created', ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement logStmt = conn.prepareStatement(logSql)) {
            ResultSet rs = stmt.executeQuery();
            System.out.println("Accounts Created Report:");
            while (rs.next()) {
                System.out.printf("Account: %s, Type: %s, Created: %s, Client ID: %d%n",
                        rs.getString("AccountNumber"),
                        rs.getString("AccountType"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getInt("ClientID"));
            }
            logStmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            logStmt.setInt(2, staff.getStaffId());
            logStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error generating accounts report: " + e.getMessage());
        }
    }

    public void generateTransactionCountReport() {
        String sql = "SELECT SourceAccount, COUNT(TransactionID) AS TransactionCount FROM TRANSACTION GROUP BY SourceAccount";
        String logSql = "INSERT INTO REPORTS (Type, Timestamp, StaffID) VALUES ('Number of Transactions', ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement logStmt = conn.prepareStatement(logSql)) {
            ResultSet rs = stmt.executeQuery();
            System.out.println("Transaction Count Report:");
            while (rs.next()) {
                System.out.printf("Account: %s, Transactions: %d%n",
                        rs.getString("SourceAccount"),
                        rs.getInt("TransactionCount"));
            }
            logStmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            logStmt.setInt(2, staff.getStaffId());
            logStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error generating transaction report: " + e.getMessage());
        }
    }

    public void generateLoansApprovedReport() {
        String sql = "SELECT L.LoanNumber, L.Amount, L.LoanType, L.LoanStatus, LH.ClientID " +
                "FROM LOAN L JOIN LOAN_HOLDER LH ON L.LoanNumber = LH.LoanNumber";
        String logSql = "INSERT INTO REPORTS (Type, Timestamp, StaffID) VALUES ('Loan Approved', ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement logStmt = conn.prepareStatement(logSql)) {
            ResultSet rs = stmt.executeQuery();
            System.out.println("Loans Approved Report:");
            while (rs.next()) {
                System.out.printf("Loan: %s, Amount: %.2f, Type: %s, Status: %s, Client ID: %d%n",
                        rs.getString("LoanNumber"),
                        rs.getDouble("Amount"),
                        rs.getString("LoanType"),
                        rs.getString("LoanStatus"),
                        rs.getInt("ClientID"));
            }
            logStmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            logStmt.setInt(2, staff.getStaffId());
            logStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error generating loans report: " + e.getMessage());
        }
    }

    public List<Transaction> viewTransactionHistory(String accountNumber) {
        return transactionDAO.getTransactionsByAccount(accountNumber);
    }
}