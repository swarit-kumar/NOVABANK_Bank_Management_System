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

public class SuspendAccountController {
    @FXML private TextField accountNumberField;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void handleSuspend() {
        String accountNumber = accountNumberField.getText();

        if (accountNumber.isEmpty()) {
            statusLabel.setText("Account number is required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
            return;
        }

        boolean success = staffService.suspendAccount(accountNumber);
        if (success) {
            showSuccessAnimation("Account suspended successfully.");
            accountNumberField.clear();
        } else {
            statusLabel.setText("Failed to suspend account. Check account number.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }

    @FXML
    private void handleCancel() {
        accountNumberField.clear();
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: #ff5252;");
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