package com.bank;

import com.bank.model.Admin;
import com.bank.model.Client;
import com.bank.model.Staff;
import com.bank.service.AccountService;
import com.bank.service.AdminService;
import com.bank.service.ClientService;
import com.bank.service.FeedbackService;
import com.bank.service.LoanService;
import com.bank.service.StaffService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    // Replicate session variables from Main.java
    public static Client currentClient = null;
    public static Staff currentStaff = null;
    public static Admin currentAdmin = null;

    // Replicate service initializations from Main.java
    public static ClientService clientService = new ClientService();
    public static AccountService accountService = new AccountService();
    public static LoanService loanService = new LoanService();
    public static FeedbackService feedbackService = new FeedbackService();
    public static StaffService staffService = null; // Initialized after staff login
    public static AdminService adminService = null; // Initialized after admin login
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/login.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        primaryStage.setTitle("Bank Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}