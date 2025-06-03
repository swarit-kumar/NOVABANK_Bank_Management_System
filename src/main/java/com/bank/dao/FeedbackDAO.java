package com.bank.dao;

import com.bank.model.Feedback;
import com.bank.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
    // Submit feedback
    public boolean submitFeedback(Feedback feedback) {
        String sql = "INSERT INTO FEEDBACK (ClientID, Message, Timestamp) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, feedback.getClientId());
            stmt.setString(2, feedback.getMessage());
            stmt.setTimestamp(3, Timestamp.valueOf(feedback.getTimestamp()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all feedback for a client
    public List<Feedback> getFeedbackByClient(int clientId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM FEEDBACK WHERE ClientID = ? ORDER BY Timestamp DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(extractFeedbackFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    // Helper method to extract Feedback from ResultSet
    private Feedback extractFeedbackFromResultSet(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setClientId(rs.getInt("ClientID"));
        feedback.setMessage(rs.getString("Message"));
        feedback.setTimestamp(rs.getTimestamp("Timestamp").toLocalDateTime());
        return feedback;
    }
}