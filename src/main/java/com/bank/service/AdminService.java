package com.bank.service;

import com.bank.dao.*;
import com.bank.model.*;
import com.bank.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {
    private Admin admin;
    private StaffDAO staffDAO;
    private ClientDAO clientDAO;
    private LoanDAO loanDAO;

    public AdminService(Admin admin) {
        this.admin = admin;
        this.staffDAO = new StaffDAO();
        this.clientDAO = new ClientDAO();
        this.loanDAO = new LoanDAO();
    }

    public List<Staff> viewAllStaff() {
        try {
            return staffDAO.getAllStaff();
        } catch (SQLException e) {
            System.out.println("Error fetching staff details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<String, Double> getAverageLoanAmountsByType() {
        String sql = "SELECT LoanType, AVG(Amount) AS AvgAmount FROM LOAN GROUP BY LoanType";
        Map<String, Double> averages = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                averages.put(rs.getString("LoanType"), rs.getDouble("AvgAmount"));
            }
        } catch (SQLException e) {
            System.out.println("Error calculating average loan amounts: " + e.getMessage());
        }
        return averages;
    }

    public Map<Staff, Integer> getStaffLoanAnalytics() {
        String sql = "SELECT S.StaffID, S.first_name, S.last_name, S.Email, S.Role, COUNT(LH.LoanNumber) AS LoanCount " +
                "FROM STAFF S LEFT JOIN LOAN_HOLDER LH ON S.StaffID = LH.StaffID " +
                "GROUP BY S.StaffID, S.first_name, S.last_name, S.Email, S.Role";
        Map<Staff, Integer> analytics = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Staff staff = new Staff(
                        rs.getInt("StaffID"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("Email"),
                        rs.getString("Role")
                );
                analytics.put(staff, rs.getInt("LoanCount"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching staff loan analytics: " + e.getMessage());
        }
        return analytics;
    }

    public boolean approveLoan(String loanNumber) {
        String sql = "UPDATE LOAN SET LoanStatus = 'Approved' WHERE LoanNumber = ? AND LoanStatus = 'Pending'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loanNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error approving loan: " + e.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> getAllLoansWithDetails() {
        String sql = "SELECT L.LoanNumber, L.Amount, L.LoanType, L.LoanStatus, " +
                "C.ClientID, C.first_name, C.last_name, S.StaffID, S.first_name AS staff_first_name, S.last_name AS staff_last_name " +
                "FROM LOAN L " +
                "JOIN LOAN_HOLDER LH ON L.LoanNumber = LH.LoanNumber " +
                "JOIN CLIENT C ON LH.ClientID = C.ClientID " +
                "JOIN STAFF S ON LH.StaffID = S.StaffID";

        List<Map<String, Object>> loans = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> loan = new HashMap<>();
                loan.put("LoanNumber", rs.getString("LoanNumber"));
                loan.put("Amount", rs.getDouble("Amount"));
                loan.put("LoanType", rs.getString("LoanType"));
                loan.put("LoanStatus", rs.getString("LoanStatus"));
                loan.put("ClientID", rs.getInt("ClientID"));
                loan.put("ClientName", rs.getString("first_name") + " " + rs.getString("last_name"));
                loan.put("StaffID", rs.getInt("StaffID"));
                loan.put("StaffName", rs.getString("staff_first_name") + " " + rs.getString("staff_last_name"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loans: " + e.getMessage());
        }
        return loans;
    }

    public List<Client> getClientsWithoutLoans() {
        String sql = "SELECT C.ClientID, C.first_name, C.last_name, C.Email, C.PhoneNumber, C.Street, C.City, C.State, C.PinCode, C.DOB " +
                "FROM CLIENT C LEFT JOIN LOAN_HOLDER LH ON C.ClientID = LH.ClientID " +
                "WHERE LH.ClientID IS NULL";
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Client client = new Client();
                client.setClientId(rs.getInt("ClientID"));
                client.setFirstName(rs.getString("first_name"));
                client.setLastName(rs.getString("last_name"));
                client.setEmail(rs.getString("Email"));
                client.setPhoneNumber(rs.getString("PhoneNumber"));
                client.setStreet(rs.getString("Street"));
                client.setCity(rs.getString("City"));
                client.setState(rs.getString("State"));
                client.setPinCode(rs.getString("PinCode"));
                client.setDob(rs.getDate("DOB").toLocalDate());
                clients.add(client);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching clients without loans: " + e.getMessage());
        }
        return clients;
    }

    public List<Client> getClientsWithSavingsAndLoan() {
        String sql = "SELECT DISTINCT C.ClientID, C.first_name, C.last_name, C.Email, C.PhoneNumber, C.Street, C.City, C.State, C.PinCode, C.DOB " +
                "FROM CLIENT C " +
                "JOIN ACCOUNT A ON C.ClientID = A.ClientID " +
                "JOIN LOAN_HOLDER LH ON C.ClientID = LH.ClientID " +
                "WHERE A.AccountType = 'Savings'";
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Client client = new Client();
                client.setClientId(rs.getInt("ClientID"));
                client.setFirstName(rs.getString("first_name"));
                client.setLastName(rs.getString("last_name"));
                client.setEmail(rs.getString("Email"));
                client.setPhoneNumber(rs.getString("PhoneNumber"));
                client.setStreet(rs.getString("Street"));
                client.setCity(rs.getString("City"));
                client.setState(rs.getString("State"));
                client.setPinCode(rs.getString("PinCode"));
                client.setDob(rs.getDate("DOB").toLocalDate());
                clients.add(client);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching clients with savings and loan: " + e.getMessage());
        }
        return clients;
    }
}