package com.bank.service;

import com.bank.dao.ClientDAO;
import com.bank.model.Client;

public class ClientService {
    private ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
    }

    // Client authentication
    public Client authenticate(String email, String password) {
        return clientDAO.authenticate(email, password);
    }

    // Get client details
    public Client getClientDetails(int clientId) {
        return clientDAO.getClientById(clientId);
    }
    
    public boolean clientExists(int clientId) {
        return clientDAO.clientExists(clientId);
    }
    
    
    // Update client profile
    public boolean updateClientProfile(Client client) {
        // Validate input
        if (client == null || client.getFirstName() == null || client.getFirstName().isEmpty() ||
                client.getLastName() == null || client.getLastName().isEmpty() ||
                client.getEmail() == null || client.getEmail().isEmpty() ||
                client.getPassword() == null || client.getPassword().isEmpty() ||
                client.getPhoneNumber() == null || client.getPhoneNumber().isEmpty()) {
            return false;
        }

        return clientDAO.updateClient(client);
    }

    // Change password
    public boolean changePassword(int clientId, String oldPassword, String newPassword) {
        Client client = clientDAO.getClientById(clientId);
        if (client == null || !client.getPassword().equals(oldPassword)) {
            return false;
        }

        client.setPassword(newPassword);
        return clientDAO.updateClient(client);
    }
}