package com.bank.dao;

import com.bank.model.Admin;
import com.bank.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    public Admin login(String email, String password) throws SQLException {
        String sql = "SELECT AdminID, first_name, last_name, Email FROM ADMIN WHERE Email = ? AND Password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Admin(
                        rs.getInt("AdminID"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("Email")
                );
            }
        }
        return null;
    }
}