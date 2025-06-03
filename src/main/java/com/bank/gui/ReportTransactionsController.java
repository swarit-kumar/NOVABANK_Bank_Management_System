package com.bank.gui;

import com.bank.MainFX;
import com.bank.service.StaffService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReportTransactionsController {
    @FXML private TableView<TransactionCount> transactionsTable;
    @FXML private TableColumn<TransactionCount, String> accountNumberCol;
    @FXML private TableColumn<TransactionCount, Number> countCol;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void initialize() {
        accountNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountNumber()));
        countCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCount()));
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleGenerateReport() {
        try {
            ObservableList<TransactionCount> data = FXCollections.observableArrayList();
            String sql = "SELECT SourceAccount, COUNT(TransactionID) AS TransactionCount FROM TRANSACTION GROUP BY SourceAccount";
            try (java.sql.Connection conn = com.bank.util.DBUtil.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.add(new TransactionCount(rs.getString("SourceAccount"), rs.getInt("TransactionCount")));
                }
            }
            transactionsTable.setItems(data);
            if (data.isEmpty()) {
                statusLabel.setText("No transactions found.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
            } else {
                statusLabel.setText("");
            }
        } catch (Exception e) {
            statusLabel.setText("Error generating report: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }

    public static class TransactionCount {
        private final String accountNumber;
        private final int count;

        public TransactionCount(String accountNumber, int count) {
            this.accountNumber = accountNumber;
            this.count = count;
        }

        public String getAccountNumber() { return accountNumber; }
        public int getCount() { return count; }
    }
}