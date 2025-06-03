package com.bank.dao;

import com.bank.model.Account;
import com.bank.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    // Get all accounts for a client
    public List<Account> getAccountsByClientId(int clientId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM ACCOUNT WHERE ClientID = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(extractAccountFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    // Get account by account number
    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM ACCOUNT WHERE AccountNumber = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAccountFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update account balance
    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE ACCOUNT SET Balance = ? WHERE AccountNumber = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to extract Account from ResultSet
    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountNumber(rs.getString("AccountNumber"));
        account.setClientId(rs.getInt("ClientID"));
        account.setAccountType(rs.getString("AccountType"));
        account.setBalance(rs.getDouble("Balance"));
        account.setStatus(rs.getString("Status"));
        account.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        account.setCreatedBy(rs.getInt("CreatedBy"));
        return account;
    }
    // Add this to your AccountDAO class
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM ACCOUNT";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }


}
