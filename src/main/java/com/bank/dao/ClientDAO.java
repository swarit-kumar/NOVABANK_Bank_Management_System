package com.bank.dao;

import com.bank.model.Client;
import com.bank.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    // Client authentication
    public Client authenticate(String email, String password) {
        String sql = "SELECT * FROM CLIENT WHERE Email = ? AND Password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractClientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get client by ID
    public Client getClientById(int clientId) {
        String sql = "SELECT * FROM CLIENT WHERE ClientID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractClientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update client information
    public boolean updateClient(Client client) {
        String sql = "UPDATE CLIENT SET first_name = ?, last_name = ?, Email = ?, Password = ?, " +
                "PhoneNumber = ?, street = ?, city = ?, state = ?, pincode = ?, DOB = ? " +
                "WHERE ClientID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getFirstName());
            stmt.setString(2, client.getLastName());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getPassword());
            stmt.setString(5, client.getPhoneNumber());
            stmt.setString(6, client.getStreet());
            stmt.setString(7, client.getCity());
            stmt.setString(8, client.getState());
            stmt.setString(9, client.getPinCode());
            stmt.setDate(10, Date.valueOf(client.getDob()));
            stmt.setInt(11, client.getClientId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to extract Client from ResultSet
    private Client extractClientFromResultSet(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setClientId(rs.getInt("ClientID"));
        client.setFirstName(rs.getString("first_name"));
        client.setLastName(rs.getString("last_name"));
        client.setEmail(rs.getString("Email"));
        client.setPassword(rs.getString("Password"));
        client.setPhoneNumber(rs.getString("PhoneNumber"));
        client.setStreet(rs.getString("street"));
        client.setCity(rs.getString("city"));
        client.setState(rs.getString("state"));
        client.setPinCode(rs.getString("pincode"));
        client.setDob(rs.getDate("DOB").toLocalDate());
        return client;
    }

    // Search clients by name or email
    public List<Client> searchClients(String searchTerm) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM CLIENT WHERE first_name LIKE ? OR last_name LIKE ? OR Email LIKE ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(extractClientFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
    
    public boolean clientExists(int clientId) {
        String query = "SELECT 1 FROM client WHERE ClientID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If there's any result, the client exists
        } catch (SQLException e) {
            e.printStackTrace(); // Or log this
            return false;
        }
    }
    
}