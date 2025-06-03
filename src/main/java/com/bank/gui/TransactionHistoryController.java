package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Transaction;
import com.bank.service.StaffService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TransactionHistoryController {
    @FXML private TextField accountNumberField;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> transactionIdCol;
    @FXML private TableColumn<Transaction, String> typeCol;
    @FXML private TableColumn<Transaction, Number> amountCol;
    @FXML private TableColumn<Transaction, String> dateCol;
    @FXML private TableColumn<Transaction, String> sourceCol;
    @FXML private TableColumn<Transaction, String> destCol;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void initialize() {
        transactionIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTransactionId()));
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTransactionType()));
        amountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAmount()));
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateTime().toString()));
        sourceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSourceAccount()));
        destCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDestinationAccount() != null ? data.getValue().getDestinationAccount() : "N/A"));
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleViewHistory() {
        String accountNumber = accountNumberField.getText();

        if (accountNumber.isEmpty()) {
            statusLabel.setText("Account number is required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
            return;
        }

        transactionsTable.getItems().clear();
        transactionsTable.getItems().addAll(staffService.viewTransactionHistory(accountNumber));
        if (transactionsTable.getItems().isEmpty()) {
            statusLabel.setText("No transactions found for this account.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        } else {
            statusLabel.setText("");
        }
    }
}