package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Account;
import com.bank.model.Feedback;
import com.bank.model.Loan;
import com.bank.model.LoanPolicy;
import com.bank.model.Transaction;
import com.bank.service.AccountService;
import com.bank.service.ClientService;
import com.bank.service.FeedbackService;
import com.bank.service.LoanService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ClientDashboardController {
    @FXML private StackPane contentArea;

    private AccountService accountService = new AccountService();
    private LoanService loanService = new LoanService();
    private FeedbackService feedbackService = new FeedbackService();
    private ClientService clientService = new ClientService();

    @FXML
    private void showAccounts() {
        List<Account> accounts = accountService.getClientAccounts(MainFX.currentClient.getClientId());
        TableView<Account> table = new TableView<>();

        TableColumn<Account, String> numberCol = new TableColumn<>("Account Number");
        numberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountNumber()));

        TableColumn<Account, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountType()));

        TableColumn<Account, Number> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBalance()));

        TableColumn<Account, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        table.getColumns().addAll(numberCol, typeCol, balanceCol, statusCol);
        table.getItems().addAll(accounts);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        contentArea.getChildren().setAll(table);
    }

    @FXML
    private void showDeposit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/deposit_form.fxml"));
            Node form = loader.load();
            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            showError("Failed to load deposit form: " + e.getMessage());
        }
    }

    @FXML
    private void showWithdraw() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/withdraw_form.fxml"));
            Node form = loader.load();
            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            showError("Failed to load withdraw form: " + e.getMessage());
        }
    }

    @FXML
    private void showTransfer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/transfer_form.fxml"));
            Node form = loader.load();
            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            showError("Failed to load transfer form: " + e.getMessage());
        }
    }

    @FXML
    private void showLoans() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/loan_management.fxml"));
            Node form = loader.load();
            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            showError("Failed to load loan management: " + e.getMessage());
        }
    }

    @FXML
    private void showTransactions() {
        List<Transaction> transactions = accountService.getClientTransactions(MainFX.currentClient.getClientId());
        TableView<Transaction> table = new TableView<>();

        TableColumn<Transaction, String> idCol = new TableColumn<>("Transaction ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTransactionId()));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTransactionType()));

        TableColumn<Transaction, Number> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAmount()));

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateTime().toString()));

        TableColumn<Transaction, String> sourceCol = new TableColumn<>("Source");
        sourceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSourceAccount()));

        TableColumn<Transaction, String> destCol = new TableColumn<>("Destination");
        destCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDestinationAccount() != null ? data.getValue().getDestinationAccount() : "N/A"));

        table.getColumns().addAll(idCol, typeCol, amountCol, dateCol, sourceCol, destCol);
        table.getItems().addAll(transactions);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        contentArea.getChildren().setAll(table);
    }

    @FXML
    private void showFeedback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/feedback_form.fxml"));
            Node form = loader.load();
            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            showError("Failed to load feedback form: " + e.getMessage());
        }
    }

    @FXML
    private void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/profile_management.fxml"));
            Node form = loader.load();
            contentArea.getChildren().setAll(form);
        } catch (IOException e) {
            showError("Failed to load profile management: " + e.getMessage());
        }
    }

    @FXML
    private void logout() {
        try {
            MainFX.currentClient = null;
            Stage stage = (Stage) contentArea.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Failed to logout: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}