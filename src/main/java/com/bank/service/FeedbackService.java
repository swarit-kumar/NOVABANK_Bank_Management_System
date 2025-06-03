package com.bank.service;

import com.bank.dao.FeedbackDAO;
import com.bank.model.Feedback;

import java.time.LocalDateTime;
import java.util.List;

public class FeedbackService {
    private FeedbackDAO feedbackDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
    }

    // Submit feedback
    public boolean submitFeedback(int clientId, String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        Feedback feedback = new Feedback();
        feedback.setClientId(clientId);
        feedback.setMessage(message);
        feedback.setTimestamp(LocalDateTime.now());

        return feedbackDAO.submitFeedback(feedback);
    }

    // Get client's feedback history
    public List<Feedback> getClientFeedback(int clientId) {
        return feedbackDAO.getFeedbackByClient(clientId);
    }
}