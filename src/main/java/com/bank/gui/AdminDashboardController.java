package com.bank.gui;

import com.bank.MainFX;
import com.bank.model.Client;
import com.bank.model.Staff;
import com.bank.service.AdminService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AdminDashboardController {
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());

    @FXML private Label headerLabel;
    @FXML private StackPane contentArea;

    private AdminService adminService;
    private FilteredList<Map.Entry<Staff, Integer>> filteredAnalytics;

    @FXML
    private void initialize() {
        if (MainFX.currentAdmin != null) {
            adminService = new AdminService(MainFX.currentAdmin);
            headerLabel.setText("🏦 Nova Bank - Admin Dashboard (" + MainFX.currentAdmin.getFirstName() + " " + MainFX.currentAdmin.getLastName() + ")");
        } else {
            loadErrorView("Admin authentication required.");
            LOGGER.severe("No admin authenticated in MainFX.currentAdmin");
        }
    }

    @FXML
    private void showViewStaff() {
        TableView<Staff> table = createTableView();
        TableColumn<Staff, Integer> idCol = createColumn("Staff ID", 100, cellData -> new SimpleIntegerProperty(cellData.getValue().getStaffId()).asObject());
        TableColumn<Staff, String> nameCol = createColumn("Name", 150, cellData -> new SimpleStringProperty(
                cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
        TableColumn<Staff, String> emailCol = createColumn("Email", 200, cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        TableColumn<Staff, String> roleCol = createColumn("Role", 100, cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        table.getColumns().addAll(idCol, nameCol, emailCol, roleCol);
        try {
            table.getItems().setAll(adminService.viewAllStaff());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching staff list: ", e);
            loadErrorView("Error fetching staff: " + e.getMessage());
        }
        updateContent(table);
    }

    @FXML
    private void showAvgLoanAmounts() {
        TableView<Map.Entry<String, Double>> table = createTableView();
        TableColumn<Map.Entry<String, Double>, String> typeCol = createColumn("Loan Type", 150,
                cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        TableColumn<Map.Entry<String, Double>, Double> avgCol = createColumn("Average Amount", 150,
                cellData -> new SimpleDoubleProperty(cellData.getValue().getValue()).asObject());

        table.getColumns().addAll(typeCol, avgCol);
        try {
            table.getItems().setAll(adminService.getAverageLoanAmountsByType().entrySet());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching loan averages: ", e);
            loadErrorView("Error fetching loan averages: " + e.getMessage());
        }
        updateContent(table);
    }

    @FXML
    private void showStaffLoanAnalytics() {
        TableView<Map.Entry<Staff, Integer>> table = createTableView();
        TableColumn<Map.Entry<Staff, Integer>, Integer> idCol = createColumn("Staff ID", 100,
                cellData -> new SimpleIntegerProperty(cellData.getValue().getKey().getStaffId()).asObject());
        TableColumn<Map.Entry<Staff, Integer>, String> nameCol = createColumn("Name", 150,
                cellData -> new SimpleStringProperty(cellData.getValue().getKey().getFirstName() + " " + cellData.getValue().getKey().getLastName()));
        TableColumn<Map.Entry<Staff, Integer>, String> emailCol = createColumn("Email", 200,
                cellData -> new SimpleStringProperty(cellData.getValue().getKey().getEmail()));
        TableColumn<Map.Entry<Staff, Integer>, String> roleCol = createColumn("Role", 100,
                cellData -> new SimpleStringProperty(cellData.getValue().getKey().getRole()));
        TableColumn<Map.Entry<Staff, Integer>, Integer> countCol = createColumn("Loan Count", 100,
                cellData -> new SimpleIntegerProperty(cellData.getValue().getValue()).asObject());

        table.getColumns().addAll(idCol, nameCol, emailCol, roleCol, countCol);

        try {
            Map<Staff, Integer> analytics = adminService.getStaffLoanAnalytics();
            var items = FXCollections.observableArrayList(analytics.entrySet());
            filteredAnalytics = new FilteredList<>(items, p -> true);
            SortedList<Map.Entry<Staff, Integer>> sortedData = new SortedList<>(filteredAnalytics);

            // ✅ Set the comparator first
            sortedData.setComparator(Comparator.<Map.Entry<Staff, Integer>>comparingInt(Map.Entry::getValue).reversed());

            // ✅ Bind AFTER setting the comparator
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // Add search field
            TextField searchField = new TextField();
            searchField.setPromptText("Search by Name or ID...");
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredAnalytics.setPredicate(entry -> {
                    Staff staff = entry.getKey();
                    String search = newValue.toLowerCase();
                    return staff.getFirstName().toLowerCase().contains(search)
                            || staff.getLastName().toLowerCase().contains(search)
                            || String.valueOf(staff.getStaffId()).contains(search);
                });
            });

            table.setItems(sortedData);
            VBox container = new VBox(10, searchField, table);
            container.setAlignment(javafx.geometry.Pos.CENTER);
            updateContent(container);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching staff loan analytics: ", e);
            loadErrorView("Error fetching analytics: " + e.getMessage());
        }
    }


    @FXML
    private void showAllLoans() {
        TableView<Map<String, Object>> table = createTableView();
        TableColumn<Map<String, Object>, String> loanNumCol = createColumn("Loan Number", 120,
                cellData -> new SimpleStringProperty((String) cellData.getValue().get("LoanNumber")));
        TableColumn<Map<String, Object>, Double> amountCol = createColumn("Amount", 100,
                cellData -> new SimpleDoubleProperty((Double) cellData.getValue().get("Amount")).asObject());
        TableColumn<Map<String, Object>, String> typeCol = createColumn("Loan Type", 100,
                cellData -> new SimpleStringProperty((String) cellData.getValue().get("LoanType")));
        TableColumn<Map<String, Object>, String> statusCol = createColumn("Status", 100,
                cellData -> new SimpleStringProperty((String) cellData.getValue().get("LoanStatus")));
        TableColumn<Map<String, Object>, String> clientCol = createColumn("Client", 150,
                cellData -> new SimpleStringProperty((String) cellData.getValue().get("ClientName")));
        TableColumn<Map<String, Object>, String> staffCol = createColumn("Staff", 150,
                cellData -> new SimpleStringProperty((String) cellData.getValue().get("StaffName")));

        table.getColumns().addAll(loanNumCol, amountCol, typeCol, statusCol, clientCol, staffCol);
        try {
            table.getItems().setAll(adminService.getAllLoansWithDetails());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching loans: ", e);
            loadErrorView("Error fetching loans: " + e.getMessage());
        }
        updateContent(table);
    }

    @FXML
    private void showClientsWithoutLoans() {
        TableView<Client> table = createTableView();
        TableColumn<Client, Integer> idCol = createColumn("Client ID", 100,
                cellData -> new SimpleIntegerProperty(cellData.getValue().getClientId()).asObject());
        TableColumn<Client, String> nameCol = createColumn("Name", 150,
                cellData -> new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
        TableColumn<Client, String> emailCol = createColumn("Email", 200,
                cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        TableColumn<Client, String> phoneCol = createColumn("Phone", 120,
                cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol);
        try {
            table.getItems().setAll(adminService.getClientsWithoutLoans());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching clients without loans: ", e);
            loadErrorView("Error fetching clients: " + e.getMessage());
        }
        updateContent(table);
    }

    @FXML
    private void showClientsWithSavingsLoan() {
        TableView<Client> table = createTableView();
        TableColumn<Client, Integer> idCol = createColumn("Client ID", 100,
                cellData -> new SimpleIntegerProperty(cellData.getValue().getClientId()).asObject());
        TableColumn<Client, String> nameCol = createColumn("Name", 150,
                cellData -> new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
        TableColumn<Client, String> emailCol = createColumn("Email", 200,
                cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        TableColumn<Client, String> phoneCol = createColumn("Phone", 120,
                cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol);
        try {
            table.getItems().setAll(adminService.getClientsWithSavingsAndLoan());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching clients with savings and loan: ", e);
            loadErrorView("Error fetching clients: " + e.getMessage());
        }
        updateContent(table);
    }

    @FXML
    private void showApproveLoan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/approve_loan_form.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading approve loan form: ", e);
            loadErrorView("Error loading form: " + e.getMessage());
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

    private <T, U> TableColumn<T, U> createColumn(String title, double width, javafx.util.Callback<javafx.scene.control.TableColumn.CellDataFeatures<T, U>, javafx.beans.value.ObservableValue<U>> cellValueFactory) {
        TableColumn<T, U> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(cellValueFactory);
        return column;
    }

    private <T> TableView<T> createTableView() {
        TableView<T> table = new TableView<>();
        table.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #e0e0e0;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void updateContent(javafx.scene.Node node) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }

    private void loadErrorView(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: #ff5252; -fx-font-size: 16px; -fx-font-family: 'Segoe UI';");
        updateContent(errorLabel);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
