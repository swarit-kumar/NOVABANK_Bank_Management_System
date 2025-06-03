package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Account;
import com.bank.service.StaffService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ViewAccountsController {
    @FXML private TextField clientIdField;
    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, String> accountNumberCol;
    @FXML private TableColumn<Account, String> accountTypeCol;
    @FXML private TableColumn<Account, Number> balanceCol;
    @FXML private TableColumn<Account, String> statusCol;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void initialize() {
        accountNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountNumber()));
        accountTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountType()));
        balanceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBalance()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        accountsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleViewAccounts() {
        String clientIdStr = clientIdField.getText();

        if (clientIdStr.isEmpty()) {
            statusLabel.setText("Client ID is required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
            return;
        }

        try {
            int clientId = Integer.parseInt(clientIdStr);
            accountsTable.getItems().clear();
            accountsTable.getItems().addAll(staffService.viewClientAccounts(clientId));
            if (accountsTable.getItems().isEmpty()) {
                statusLabel.setText("No accounts found for this client.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
            } else {
                statusLabel.setText("");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid client ID format.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }
}