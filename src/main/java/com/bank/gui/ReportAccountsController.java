package com.bank.gui;

import com.bank.model.Account;
import com.bank.dao.AccountDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReportAccountsController {
    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, String> accountNumberCol;
    @FXML private TableColumn<Account, String> accountTypeCol;
    @FXML private TableColumn<Account, String> createdAtCol;
    @FXML private TableColumn<Account, Number> clientIdCol;
    @FXML private Label statusLabel;

    private AccountDAO accountDAO = new AccountDAO();

    @FXML
    private void initialize() {
        accountNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountNumber()));
        accountTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountType()));
        createdAtCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt().toString()));
        clientIdCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getClientId()));
        accountsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleGenerateReport() {
        try {
            accountsTable.getItems().clear();
            accountsTable.getItems().addAll(accountDAO.getAllAccounts());
            if (accountsTable.getItems().isEmpty()) {
                statusLabel.setText("No accounts found.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
            } else {
                statusLabel.setText("");
            }
        } catch (Exception e) {
            statusLabel.setText("Error generating report: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }
}