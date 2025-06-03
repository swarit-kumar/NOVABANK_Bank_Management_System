package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Client;
import com.bank.service.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProfileController {
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label dobLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField streetField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField pinCodeField;
    @FXML private Label updateErrorLabel;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordErrorLabel;

    private ClientService clientService = new ClientService();

    @FXML
    private void initialize() {
        Client client = clientService.getClientDetails(MainFX.currentClient.getClientId());
        nameLabel.setText("Name: " + client.getFirstName() + " " + client.getLastName());
        emailLabel.setText("Email: " + client.getEmail());
        phoneLabel.setText("Phone: " + client.getPhoneNumber());
        addressLabel.setText("Address: " + client.getStreet() + ", " + client.getCity() + ", " +
                client.getState() + " - " + client.getPinCode());
        dobLabel.setText("DOB: " + client.getDob().toString());

        // Pre-fill update form
        firstNameField.setText(client.getFirstName());
        lastNameField.setText(client.getLastName());
        emailField.setText(client.getEmail());
        phoneField.setText(client.getPhoneNumber());
        streetField.setText(client.getStreet());
        cityField.setText(client.getCity());
        stateField.setText(client.getState());
        pinCodeField.setText(client.getPinCode());
    }

    @FXML
    private void handleUpdate() {
        Client client = clientService.getClientDetails(MainFX.currentClient.getClientId());
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String street = streetField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String pinCode = pinCodeField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            updateErrorLabel.setText("Required fields cannot be empty.");
            return;
        }

        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setPhoneNumber(phone);
        client.setStreet(street);
        client.setCity(city);
        client.setState(state);
        client.setPinCode(pinCode);

        boolean success = clientService.updateClientProfile(client);
        if (success) {
            updateErrorLabel.setText("Profile updated.");
            updateErrorLabel.setStyle("-fx-text-fill: green;");
            MainFX.currentClient = client;
            nameLabel.setText("Name: " + firstName + " " + lastName);
            emailLabel.setText("Email: " + email);
            phoneLabel.setText("Phone: " + phone);
            addressLabel.setText("Address: " + street + ", " + city + ", " + state + " - " + pinCode);
        } else {
            updateErrorLabel.setText("Failed to update profile.");
        }
    }

    @FXML
    private void handleClear() {
        Client client = clientService.getClientDetails(MainFX.currentClient.getClientId());
        firstNameField.setText(client.getFirstName());
        lastNameField.setText(client.getLastName());
        emailField.setText(client.getEmail());
        phoneField.setText(client.getPhoneNumber());
        streetField.setText(client.getStreet());
        cityField.setText(client.getCity());
        stateField.setText(client.getState());
        pinCodeField.setText(client.getPinCode());
        updateErrorLabel.setText("");
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            passwordErrorLabel.setText("All fields are required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            passwordErrorLabel.setText("New passwords do not match.");
            return;
        }

        boolean success = clientService.changePassword(MainFX.currentClient.getClientId(), currentPassword, newPassword);
        if (success) {
            passwordErrorLabel.setText("Password changed.");
            passwordErrorLabel.setStyle("-fx-text-fill: green;");
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            passwordErrorLabel.setText("Failed to change password. Check current password.");
        }
    }

    @FXML
    private void handleClearPassword() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        passwordErrorLabel.setText("");
    }
}