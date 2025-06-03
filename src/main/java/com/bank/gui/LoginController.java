package com.bank.gui;

import com.bank.MainFX; // Import MainFX for session variables and services
import com.bank.dao.AdminDAO;
import com.bank.dao.StaffDAO;
import com.bank.service.AdminService;
import com.bank.service.StaffService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> userTypeCombo;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String userType = userTypeCombo.getValue();

        if (username.isEmpty() || password.isEmpty() || userType == null) {
            errorLabel.setText("Please fill all fields.");
            return;
        }

        try {
            boolean authenticated = false;
            String dashboardFxml = "";

            switch (userType) {
                case "Client":
                    MainFX.currentClient = MainFX.clientService.authenticate(username, password);
                    if (MainFX.currentClient != null) {
                        authenticated = true;
                        dashboardFxml = "/com/bank/client_dashboard.fxml";
                    }
                    break;
                case "Staff":
                    StaffDAO staffDAO = new StaffDAO();
                    MainFX.currentStaff = staffDAO.login(username, password);
                    if (MainFX.currentStaff != null) {
                        MainFX.staffService = new StaffService(MainFX.currentStaff);
                        authenticated = true;
                        dashboardFxml = "/com/bank/staff_dashboard.fxml";
                    }
                    break;
                case "Admin":
                    AdminDAO adminDAO = new AdminDAO();
                    MainFX.currentAdmin = adminDAO.login(username, password);
                    if (MainFX.currentAdmin != null) {
                        MainFX.adminService = new AdminService(MainFX.currentAdmin);
                        authenticated = true;
                        dashboardFxml = "/com/bank/admin_dashboard.fxml";
                    }
                    break;
            }

            if (authenticated) {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardFxml));
                Scene scene = new Scene(loader.load(), 800, 600);
                stage.setScene(scene);
            } else {
                errorLabel.setText("Invalid credentials.");
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        userTypeCombo.getSelectionModel().clearSelection();
        errorLabel.setText("");
    }
}