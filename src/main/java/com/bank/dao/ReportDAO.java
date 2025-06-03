package com.bank.dao;

import com.bank.model.Report;
import com.bank.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    // Create a new report
    public boolean createReport(String type, int staffId) {
        String sql = "INSERT INTO REPORTS (Type, StaffID) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);
            stmt.setInt(2, staffId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all reports
    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.*, s.first_name, s.last_name FROM REPORTS r " +
                "JOIN STAFF s ON r.StaffID = s.StaffID ORDER BY r.Timestamp DESC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reports.add(extractReportFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    // Get reports by staff member
    public List<Report> getReportsByStaff(int staffId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.*, s.first_name, s.last_name FROM REPORTS r " +
                "JOIN STAFF s ON r.StaffID = s.StaffID WHERE r.StaffID = ? ORDER BY r.Timestamp DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(extractReportFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    // Helper method to extract Report from ResultSet
    private Report extractReportFromResultSet(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportId(rs.getInt("ReportID"));
        report.setType(rs.getString("Type"));
        report.setTimestamp(rs.getTimestamp("Timestamp").toLocalDateTime());
        report.setStaffId(rs.getInt("StaffID"));
        report.setStaffName(rs.getString("first_name") + " " + rs.getString("last_name"));
        return report;
    }
}