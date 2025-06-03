package com.bank.gui;

import com.bank.MainFX;
import com.bank.service.AdminService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ApproveLoanController {
    private static final Logger LOGGER = Logger.getLogger(ApproveLoanController.class.getName());

    @FXML private TextField loanNumberField;
    @FXML private Label statusLabel;

    private AdminService adminService;

    @FXML
    private void initialize() {
        if (MainFX.currentAdmin != null) {
            adminService = new AdminService(MainFX.currentAdmin);
        } else {
            showError("Admin authentication required.");
            LOGGER.severe("No admin authenticated in MainFX.currentAdmin");
        }
    }

    @FXML
    private void handleApprove() {
        if (adminService == null) {
            showError("Admin authentication required. Please log in again.");
            return;
        }

        String loanNumber = loanNumberField.getText().trim();

        if (loanNumber.isEmpty()) {
            showError("Loan Number is required.");
            return;
        }

        try {
            boolean success = adminService.approveLoan(loanNumber);
            if (success) {
                showSuccess("Loan " + loanNumber + " approved successfully.");
                loanNumberField.clear();
            } else {
                showError("Failed to approve loan. Check Loan Number or status.");
                LOGGER.warning("Loan approval failed for Loan Number: " + loanNumber);
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Exception during loan approval: ", e);
        }
    }

    @FXML
    private void handleClear() {
        loanNumberField.clear();
        statusLabel.setText("");
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
        fadeOut.setDelay(Duration.millis(2000));

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(500), statusLabel);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), statusLabel);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        scaleDown.setDelay(Duration.millis(1000));

        ParallelTransition parallel = new ParallelTransition(fadeIn, scaleUp, scaleDown);
        parallel.setOnFinished(e -> fadeOut.play());
        parallel.play();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #ff5252;");
    }
}