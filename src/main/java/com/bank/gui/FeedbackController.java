package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Feedback;
import com.bank.service.FeedbackService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FeedbackController {
    @FXML private TextArea messageArea;
    @FXML private Label errorLabel;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> timestampCol;
    @FXML private TableColumn<Feedback, String> messageCol;

    private FeedbackService feedbackService = new FeedbackService();

    @FXML
    private void initialize() {
        timestampCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toString()));
        messageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMessage()));
        feedbackTable.getItems().setAll(feedbackService.getClientFeedback(MainFX.currentClient.getClientId()));
    }

    @FXML
    private void handleSubmit() {
        String message = messageArea.getText().trim();
        if (message.isEmpty()) {
            errorLabel.setText("Feedback message cannot be empty.");
            return;
        }

        boolean success = feedbackService.submitFeedback(MainFX.currentClient.getClientId(), message);
        if (success) {
            errorLabel.setText("Feedback submitted.");
            errorLabel.setStyle("-fx-text-fill: green;");
            messageArea.clear();
            feedbackTable.getItems().setAll(feedbackService.getClientFeedback(MainFX.currentClient.getClientId()));
        } else {
            errorLabel.setText("Failed to submit feedback.");
        }
    }

    @FXML
    private void handleCancel() {
        messageArea.clear();
        errorLabel.setText("");
    }
}