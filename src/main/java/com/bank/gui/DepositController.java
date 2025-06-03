package com.bank.gui;

import com.bank.MainFX;
import com.bank.service.AccountService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class DepositController {
    @FXML private TextField accountNumberField;
    @FXML private TextField amountField;
    @FXML private Label statusLabel;

    private AccountService accountService = new AccountService();

    @FXML
    private void handleDeposit() {
        String accountNumber = accountNumberField.getText();
        String amountStr = amountField.getText();

        if (accountNumber.isEmpty() || amountStr.isEmpty()) {
            statusLabel.setText("All fields are required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;"); // Error in red
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                statusLabel.setText("Amount must be positive.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
                return;
            }

            boolean success = accountService.deposit(accountNumber, amount);
            if (success) {
                showSuccessAnimation("Deposit successful.");
                accountNumberField.clear();
                amountField.clear();
            } else {
                statusLabel.setText("Deposit failed. Check account number and status.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid amount format.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }

    @FXML
    private void handleCancel() {
        accountNumberField.clear();
        amountField.clear();
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: #ff5252;"); // Reset to error style
    }

    private void showSuccessAnimation(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #4ade80;"); // Success in green

        // Fade animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), statusLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), statusLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(1500)); // Hold for 1.5s

        // Scale animation
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(500), statusLabel);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), statusLabel);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        scaleDown.setDelay(Duration.millis(1000)); // Start scaling down after 1s

        // Combine animations
        ParallelTransition parallel = new ParallelTransition(fadeIn, scaleUp, scaleDown);
        parallel.setOnFinished(e -> {
            fadeOut.play(); // Fade out after parallel completes
            fadeOut.setOnFinished(ev -> statusLabel.setText("")); // Clear after fade-out
        });

        parallel.play();
    }
}