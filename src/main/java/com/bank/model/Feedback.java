package com.bank.model;

import java.time.LocalDateTime;

public class Feedback {
    private int clientId;
    private String message;
    private LocalDateTime timestamp;

    // Constructors
    public Feedback() {}

    public Feedback(int clientId, String message, LocalDateTime timestamp) {
        this.clientId = clientId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Feedback{" +
                "clientId=" + clientId +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}