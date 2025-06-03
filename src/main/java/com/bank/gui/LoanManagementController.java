package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Loan;
import com.bank.model.LoanPayment;
import com.bank.model.LoanPolicy;
import com.bank.service.LoanService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class LoanManagementController {
    @FXML private TabPane loanTabs;
    @FXML private TableView<LoanPolicy> policyTable;
    @FXML private TableColumn<LoanPolicy, String> policyTypeCol;
    @FXML private TableColumn<LoanPolicy, Number> interestRateCol;
    @FXML private TableColumn<LoanPolicy, Number> maxAmountCol;
    @FXML private TableView<Loan> loanTable;
    @FXML private TableColumn<Loan, String> loanNumberCol;
    @FXML private TableColumn<Loan, Number> loanAmountCol;
    @FXML private TableColumn<Loan, String> loanTypeCol;
    @FXML private TableColumn<Loan, String> loanStatusCol;
    @FXML private TextField loanTypeField;
    @FXML private TextField amountField;
    @FXML private Label applyErrorLabel;
    @FXML private TextField loanNumberField;
    @FXML private TextField paymentAmountField;
    @FXML private TableView<LoanPayment> paymentTable;
    @FXML private TableColumn<LoanPayment, String> paymentNumberCol;
    @FXML private TableColumn<LoanPayment, String> paymentDateCol;
    @FXML private TableColumn<LoanPayment, Number> paidAmountCol;
    @FXML private TableColumn<LoanPayment, Number> leftAmountCol;
    @FXML private Label paymentErrorLabel;

    private LoanService loanService = new LoanService();

    @FXML
    private void initialize() {
        // Initialize policy table
        policyTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanType()));
        interestRateCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getInterestRate()));
        maxAmountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getMaxAmount()));
        policyTable.getItems().setAll(loanService.getLoanPolicies());

        // Initialize loan table
        loanNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanNumber()));
        loanAmountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAmount()));
        loanTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanType()));
        loanStatusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanStatus()));

        // Initialize payment table
        paymentNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentNumber()));
        paymentDateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentDate().toString()));
        paidAmountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPaidAmount()));
        leftAmountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getLeftAmount()));

        if (MainFX.currentClient != null) {
            loanTable.getItems().setAll(loanService.getClientLoans(MainFX.currentClient.getClientId()));
        } else {
            loanTable.getItems().clear();
            applyErrorLabel.setText("No client logged in.");
        }
    }

    @FXML
    private void handleApply() {
        if (MainFX.currentClient == null) {
            applyErrorLabel.setText("No client logged in.");
            return;
        }

        String loanType = loanTypeField.getText();
        String amountStr = amountField.getText();

        if (loanType.isEmpty() || amountStr.isEmpty()) {
            applyErrorLabel.setText("All fields are required.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                applyErrorLabel.setText("Amount must be positive.");
                return;
            }

            // Using a default staffId (1) as in CLI; adjust if needed
            boolean success = loanService.applyForLoan(MainFX.currentClient.getClientId(), 1, amount, loanType);
            if (success) {
                applyErrorLabel.setText("Loan application submitted.");
                applyErrorLabel.setStyle("-fx-text-fill: green;");
                loanTable.getItems().setAll(loanService.getClientLoans(MainFX.currentClient.getClientId()));
            } else {
                applyErrorLabel.setText("Application failed. Check loan type and amount.");
            }
        } catch (NumberFormatException e) {
            applyErrorLabel.setText("Invalid amount format.");
        }
    }

    @FXML
    private void handleClear() {
        loanTypeField.clear();
        amountField.clear();
        applyErrorLabel.setText("");
    }

    @FXML
    private void viewPayments() {
        if (MainFX.currentClient == null) {
            paymentErrorLabel.setText("No client logged in.");
            return;
        }

        String loanNumber = loanNumberField.getText().trim();
        if (loanNumber.isEmpty()) {
            paymentErrorLabel.setText("Enter a loan number.");
            return;
        }

        List<Loan> clientLoans = loanService.getClientLoans(MainFX.currentClient.getClientId());
        boolean loanExists = clientLoans.stream()
                .anyMatch(loan -> loan.getLoanNumber().equalsIgnoreCase(loanNumber));

        if (!loanExists) {
            paymentErrorLabel.setText("Loan number not found or not associated with your account.");
            return;
        }

        List<LoanPayment> payments = loanService.getLoanPaymentHistory(loanNumber);
        if (payments.isEmpty()) {
            paymentErrorLabel.setText("No payment history found for loan " + loanNumber + ".");
        } else {
            paymentTable.getItems().setAll(payments);
            paymentErrorLabel.setText("");
        }
    }

    @FXML
    private void makePayment() {
        if (MainFX.currentClient == null) {
            paymentErrorLabel.setText("No client logged in.");
            return;
        }

        String loanNumber = loanNumberField.getText();
        String amountStr = paymentAmountField.getText();

        if (loanNumber.isEmpty() || amountStr.isEmpty()) {
            paymentErrorLabel.setText("All fields are required.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                paymentErrorLabel.setText("Amount must be positive.");
                return;
            }

            boolean success = loanService.makeLoanPayment(loanNumber, amount);
            if (success) {
                paymentErrorLabel.setText("Payment successful.");
                paymentErrorLabel.setStyle("-fx-text-fill: green;");
                viewPayments(); // Refresh table
            } else {
                paymentErrorLabel.setText("Payment failed. Check loan number and amount or authorization.");
            }
        } catch (NumberFormatException e) {
            paymentErrorLabel.setText("Invalid amount format.");
        }
    }

    @FXML
    private void clearPayments() {
        loanNumberField.clear();
        paymentAmountField.clear();
        paymentTable.getItems().clear();
        paymentErrorLabel.setText("");
    }
}