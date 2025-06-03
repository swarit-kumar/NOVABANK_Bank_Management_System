package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.LoanPayment;
import com.bank.service.StaffService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TrackPaymentsController {
    @FXML private TextField loanNumberField;
    @FXML private TableView<LoanPayment> paymentsTable;
    @FXML private TableColumn<LoanPayment, String> paymentNumberCol;
    @FXML private TableColumn<LoanPayment, String> paymentDateCol;
    @FXML private TableColumn<LoanPayment, Number> paidAmountCol;
    @FXML private TableColumn<LoanPayment, Number> leftAmountCol;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void initialize() {
        paymentNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentNumber()));
        paymentDateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentDate().toString()));
        paidAmountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPaidAmount()));
        leftAmountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getLeftAmount()));
        paymentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleViewPayments() {
        String loanNumber = loanNumberField.getText();

        if (loanNumber.isEmpty()) {
            statusLabel.setText("Loan number is required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
            return;
        }

        paymentsTable.getItems().clear();
        paymentsTable.getItems().addAll(staffService.trackLoanPayments(loanNumber));
        if (paymentsTable.getItems().isEmpty()) {
            statusLabel.setText("No payments found for this loan.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        } else {
            statusLabel.setText("");
        }
    }
}