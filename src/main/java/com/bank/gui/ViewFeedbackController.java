package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Feedback;
import com.bank.service.StaffService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ViewFeedbackController {
    @FXML private TextField clientIdField;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> messageCol;
    @FXML private TableColumn<Feedback, String> timestampCol;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void initialize() {
        messageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMessage()));
        timestampCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp().toString()));
        feedbackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleViewFeedback() {
        String clientIdStr = clientIdField.getText();

        if (clientIdStr.isEmpty()) {
            statusLabel.setText("Client ID is required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
            return;
        }

        try {
            int clientId = Integer.parseInt(clientIdStr);
            feedbackTable.getItems().clear();
            feedbackTable.getItems().addAll(staffService.viewClientFeedback(clientId));
            if (feedbackTable.getItems().isEmpty()) {
                statusLabel.setText("No feedback found for this client.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
            } else {
                statusLabel.setText("");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid client ID format.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }
}