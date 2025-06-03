package com.bank.gui;

import com.bank.MainFX;
import com.bank.service.StaffService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class UpdateClientInfoController {
    @FXML private TextField clientIdField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField streetField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField pincodeField;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void handleUpdate() {
        String clientIdStr = clientIdField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String street = streetField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String pincode = pincodeField.getText();

        if (clientIdStr.isEmpty() || email.isEmpty() || phone.isEmpty() || street.isEmpty() ||
                city.isEmpty() || state.isEmpty() || pincode.isEmpty()) {
            statusLabel.setText("All fields are required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
            return;
        }

        try {
            int clientId = Integer.parseInt(clientIdStr);
            boolean success = staffService.updateClientInfo(clientId, email, phone, street, city, state, pincode);
            if (success) {
                showSuccessAnimation("Client info updated successfully.");
                clearFields();
            } else {
                statusLabel.setText("Failed to update client info.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid client ID format.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }

    @FXML
    private void handleCancel() {
        clearFields();
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: #ff5252;");
    }

    private void clearFields() {
        clientIdField.clear();
        emailField.clear();
        phoneField.clear();
        streetField.clear();
        cityField.clear();
        stateField.clear();
        pincodeField.clear();
    }

    private void showSuccessAnimation(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #4ade80;");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), statusLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), statusLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(1500));

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(500), statusLabel);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), statusLabel);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        scaleDown.setDelay(Duration.millis(1000));

        ParallelTransition parallel = new ParallelTransition(fadeIn, scaleUp, scaleDown);
        parallel.setOnFinished(e -> {
            fadeOut.play();
            fadeOut.setOnFinished(ev -> statusLabel.setText(""));
        });

        parallel.play();
    }
}