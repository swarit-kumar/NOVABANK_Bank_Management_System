package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Client;
import com.bank.service.StaffService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ViewClientInfoController {
    @FXML private TextField clientIdField;
    @FXML private GridPane infoGrid;
    @FXML private Label statusLabel;

    private StaffService staffService = new StaffService(MainFX.currentStaff);

    @FXML
    private void handleViewInfo() {
        String clientIdStr = clientIdField.getText();

        if (clientIdStr.isEmpty()) {
            statusLabel.setText("Client ID is required.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
            return;
        }

        try {
            int clientId = Integer.parseInt(clientIdStr);
            Client client = staffService.viewClientInfo(clientId);
            if (client != null) {
                infoGrid.getChildren().clear();
                infoGrid.addRow(0, new Label("Name:"), new Label(client.getFirstName() + " " + client.getLastName()));
                infoGrid.addRow(1, new Label("Email:"), new Label(client.getEmail()));
                infoGrid.addRow(2, new Label("Phone:"), new Label(client.getPhoneNumber()));
                infoGrid.addRow(3, new Label("Address:"), new Label(client.getStreet() + ", " + client.getCity() + ", " + client.getState() + " - " + client.getPinCode()));
                infoGrid.addRow(4, new Label("DOB:"), new Label(client.getDob().toString()));
                statusLabel.setText("");
            } else {
                statusLabel.setText("Client not found.");
                statusLabel.setStyle("-fx-text-fill: #ff5252;");
                infoGrid.getChildren().clear();
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid client ID format.");
            statusLabel.setStyle("-fx-text-fill: #ff5252;");
        }
    }
}