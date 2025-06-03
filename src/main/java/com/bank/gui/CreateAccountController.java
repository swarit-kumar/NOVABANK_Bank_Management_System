package com.bank.gui;

import com.bank.MainFX;
import com.bank.service.ClientService;
import com.bank.service.StaffService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CreateAccountController {
    private static final Logger LOGGER = Logger.getLogger(CreateAccountController.class.getName());

    @FXML private TextField clientIdField;
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField initialBalanceField;
    @FXML private Label statusLabel;

    private StaffService staffService;
    private ClientService clientService;

    @FXML
    private void initialize() {
        // Initialize services only if currentStaff is not null
        if (MainFX.currentStaff != null) {
            staffService = new StaffService(MainFX.currentStaff);
            clientService = new ClientService();
        } else {
            statusLabel.setText("Staff authentication required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
            LOGGER.severe("No staff authenticated in MainFX.currentStaff");
        }
    }

    @FXML
    private void handleCreate() {
        // Check if services are initialized
        if (staffService == null || clientService == null) {
            showError("Staff authentication required. Please log in again.");
            return;
        }

        String clientIdStr = clientIdField.getText().trim();
        String accountType = accountTypeCombo.getValue();
        String balanceStr = initialBalanceField.getText().trim();

        // Validate inputs
        if (clientIdStr.isEmpty() || accountType == null || balanceStr.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        try {
            int clientId = Integer.parseInt(clientIdStr);
            double balance = Double.parseDouble(balanceStr);

            // Validate client existence
            if (!clientService.clientExists(clientId)) {
                showError("Client ID " + clientId + " does not exist.");
                return;
            }

            // Validate balance
            if (balance < 0) {
                showError("Initial balance cannot be negative.");
                return;
            }

            // Validate account type
            if (!isValidAccountType(accountType)) {
                showError("Invalid account type selected.");
                return;
            }

            // Attempt to create account
            boolean success = staffService.createAccount(clientId, accountType, balance);
            if (success) {
                showSuccess("Account created successfully.");
                clearFields();
            } else {
                showError("Failed to create account. Please try again.");
                LOGGER.warning("Account creation failed for Client ID: " + clientId);
            }
        } catch (NumberFormatException e) {
            showError("Invalid format for Client ID or Initial Balance.");
            LOGGER.warning("NumberFormatException: " + e.getMessage());
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Unexpected error during account creation: ", e);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        statusLabel.setText("");
    }

    private void clearFields() {
        clientIdField.clear();
        accountTypeCombo.getSelectionModel().clearSelection();
        initialBalanceField.clear();
    }

    private boolean isValidAccountType(String accountType) {
        return accountType != null && (
                accountType.equals("Savings") ||
                        accountType.equals("Current") ||
                        accountType.equals("Fixed Deposit")
        );
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #4ade80;");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), statusLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), statusLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(2000)); // Longer visibility for clarity

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
            fadeOut.setOnFinished(ev -> {
                statusLabel.setText("");
                statusLabel.setStyle(""); // Reset style
            });
        });

        parallel.play();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #ff5252;");
    }
}