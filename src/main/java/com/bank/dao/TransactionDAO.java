package com.bank.dao;

import com.bank.model.Transaction;
import com.bank.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    // Get all transactions for an account
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM TRANSACTION WHERE SourceAccount = ? OR DestinationAccount = ? ORDER BY DateTime DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setString(2, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Get all transactions for a client (across all accounts)
    public List<Transaction> getTransactionsByClient(int clientId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.* FROM TRANSACTION t " +
                "JOIN ACCOUNT a ON (t.SourceAccount = a.AccountNumber OR t.DestinationAccount = a.AccountNumber) " +
                "WHERE a.ClientID = ? ORDER BY t.DateTime DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Create a new transaction
    public boolean createTransaction(Transaction transaction) {
        String sql = "INSERT INTO TRANSACTION (TransactionID, TransactionType, Amount, DateTime, SourceAccount, DestinationAccount) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getTransactionId());
            stmt.setString(2, transaction.getTransactionType());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(transaction.getDateTime()));
            stmt.setString(5, transaction.getSourceAccount());

            if (transaction.getDestinationAccount() != null && !transaction.getDestinationAccount().isEmpty()) {
                stmt.setString(6, transaction.getDestinationAccount());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to extract Transaction from ResultSet
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getString("TransactionID"));
        transaction.setTransactionType(rs.getString("TransactionType"));
        transaction.setAmount(rs.getDouble("Amount"));
        transaction.setDateTime(rs.getTimestamp("DateTime").toLocalDateTime());
        transaction.setSourceAccount(rs.getString("SourceAccount"));
        transaction.setDestinationAccount(rs.getString("DestinationAccount"));
        return transaction;
    }
}