package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Loan;
import com.bank.service.StaffService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReportLoansController {
    @FXML private TableView<LoanReport> loansTable;
    @FXML private TableColumn<LoanReport, String> loanNumberCol;
    @FXML private TableColumn<LoanReport, Number> amountCol;
    @FXML private TableColumn<LoanReport, String> loanTypeCol;
    @FXML private TableColumn<LoanReport, String> statusCol;
    @FXML private TableColumn<LoanReport, Number> clientIdCol;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void initialize() {
        loanNumberCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanNumber()));
        amountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAmount()));
        loanTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanType()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanStatus()));
        clientIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getClientId()));
        loansTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleGenerateReport() {
        try {
            loansTable.getItems().clear();
            String sql = "SELECT L.LoanNumber, L.Amount, L.LoanType, L.LoanStatus, LH.ClientID " +
                    "FROM LOAN L JOIN LOAN_HOLDER LH ON L.LoanNumber = LH.LoanNumber";
            try (java.sql.Connection conn = com.bank.util.DBUtil.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loansTable.getItems().add(new LoanReport(
                            rs.getString("LoanNumber"),
                            rs.getDouble("Amount"),
                            rs.getString("LoanType"),
                            rs.getString("LoanStatus"),
                            rs.getInt("ClientID")
                    ));
                }
            }
            if (loansTable.getItems().isEmpty()) {
                statusLabel.setText("No loans found.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
            } else {
                statusLabel.setText("");
            }
        } catch (Exception e) {
            statusLabel.setText("Error generating report: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }

    public static class LoanReport {
        private final String loanNumber;
        private final double amount;
        private final String loanType;
        private final String loanStatus;
        private final int clientId;

        public LoanReport(String loanNumber, double amount, String loanType, String loanStatus, int clientId) {
            this.loanNumber = loanNumber;
            this.amount = amount;
            this.loanType = loanType;
            this.loanStatus = loanStatus;
            this.clientId = clientId;
        }

        public String getLoanNumber() { return loanNumber; }
        public double getAmount() { return amount; }
        public String getLoanType() { return loanType; }
        public String getLoanStatus() { return loanStatus; }
        public int getClientId() { return clientId; }
    }
}