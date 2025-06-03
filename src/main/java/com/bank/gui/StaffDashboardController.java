package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Staff;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class StaffDashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label headerLabel;

    @FXML
    private void initialize() {
        Staff staff = MainFX.currentStaff;
        if (staff != null) {
            headerLabel.setText("🏦 Nova Bank - Staff Dashboard (" + staff.getFirstName() + " " + staff.getLastName() + ", " + staff.getRole() + ")");
        }
    }

    @FXML
    private void showCreateAccount() { loadForm("/com/bank/create_account_form.fxml", "Failed to load create account form"); }

    @FXML
    private void showCloseAccount() { loadForm("/com/bank/close_account_form.fxml", "Failed to load close account form"); }

    @FXML
    private void showSuspendAccount() { loadForm("/com/bank/suspend_account_form.fxml", "Failed to load suspend account form"); }

    @FXML
    private void showDeposit() { loadForm("/com/bank/deposit_form.fxml", "Failed to load deposit form"); }

    @FXML
    private void showWithdraw() { loadForm("/com/bank/withdraw_form.fxml", "Failed to load withdraw form"); }

    @FXML
    private void showTransfer() { loadForm("/com/bank/transfer_form.fxml", "Failed to load transfer form"); }

    @FXML
    private void showViewAccounts() { loadForm("/com/bank/view_accounts_form.fxml", "Failed to load view accounts form"); }

    @FXML
    private void showLoanPayment() { loadForm("/com/bank/loan_payment_form.fxml", "Failed to load loan payment form"); }

    @FXML
    private void showTrackPayments() { loadForm("/com/bank/track_payments_form.fxml", "Failed to load track payments form"); }

    @FXML
    private void showUpdateLoanStatus() { loadForm("/com/bank/update_loan_status_form.fxml", "Failed to load update loan status form"); }

    @FXML
    private void showViewClientInfo() { loadForm("/com/bank/view_client_info_form.fxml", "Failed to load view client info form"); }

    @FXML
    private void showUpdateClientInfo() { loadForm("/com/bank/update_client_info_form.fxml", "Failed to load update client info form"); }

    @FXML
    private void showViewFeedback() { loadForm("/com/bank/view_feedback_form.fxml", "Failed to load view feedback form"); }

    @FXML
    private void showReportAccounts() { loadForm("/com/bank/report_accounts_form.fxml", "Failed to load accounts report form"); }

    @FXML
    private void showReportTransactions() { loadForm("/com/bank/report_transactions_form.fxml", "Failed to load transactions report form"); }

    @FXML
    private void showReportLoans() { loadForm("/com/bank/report_loans_form.fxml", "Failed to load loans report form"); }

    @FXML
    private void showTransactionHistory() { loadForm("/com/bank/transaction_history_form.fxml", "Failed to load transaction history form"); }

    @FXML
    private void logout() {
        try {
            MainFX.currentStaff = null;
            Stage stage = (Stage) contentArea.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Failed to logout: " + e.getMessage());
        }
    }

    private void loadForm(String resource, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Node form = loader.load();
            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            showError(errorMessage + ": " + e.getMessage());
        }
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}