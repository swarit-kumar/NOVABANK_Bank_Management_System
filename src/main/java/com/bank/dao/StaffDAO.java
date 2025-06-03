package com.bank.dao;

import com.bank.model.Staff;
import com.bank.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    public Staff login(String email, String password) throws SQLException {
        String sql = "SELECT StaffID, first_name, last_name, Email, Role FROM STAFF WHERE Email = ? AND Password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Staff(
                        rs.getInt("StaffID"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("Email"),
                        rs.getString("Role")
                );
            }
        }
        return null;
    }

    public List<Staff> getAllStaff() throws SQLException {
        String sql = "SELECT StaffID, first_name, last_name, Email, Role FROM STAFF";
        List<Staff> staffList = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                staffList.add(new Staff(
                        rs.getInt("StaffID"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("Email"),
                        rs.getString("Role")
                ));
            }
        }
        return staffList;
    }
}