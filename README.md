# NOVABANK_Bank_Management_System

## Overview

Nova Bank is a JavaFX-based banking application designed to manage banking operations for clients, staff, and administrators. The application provides a graphical user interface (GUI) for various banking functionalities, including account management, loan processing, client management, and administrative tasks. The project leverages a MySQL database for persistent storage and follows a modular design with service, DAO, and model layers.

## Features

### Client Module

* **Account Management**: View account details, deposit funds, withdraw funds, and transfer between accounts.
* **Loan Services**: Apply for loans, view loan status, and make loan payments.
* **Profile Management**: View and edit personal information.
* **Feedback Submission**: Submit feedback to the bank.

### Staff Module

* **Account Operations**: Create new accounts, close existing accounts, and suspend accounts.
* **Loan Processing**: Process loan payments and track loan statuses.
* **Client Management**: View and edit client information.
* **Reporting**: Generate reports on accounts, transactions, and loans.

### Admin Module

* **Staff Management**: View all staff details.
* **Loan Analytics**: Analyze average loan amounts by type.
* **Staff Loan Analytics**: View staff loan data, sorted by maximum loans and searchable by name or ID.
* **Loan Oversight**: View all loans with details, identify clients without loans or with both savings and loans.
* **Loan Approval**: Approve pending loans.

### UI/UX

* **Dark Theme**: Consistent styling with colors #121212 and #1e1e1e.
* **Animations**: Fade-in/out and scale animations for feedback.
* **Responsive Design**: Tables and forms adjust to window size.

## Technologies Used

* **Language**: Java 11+
* **Framework**: JavaFX for GUI
* **Database**: MySQL
* **Build Tool**: Maven
* **Libraries**: Java SQL (JDBC), JavaFX SDK
* **Development Environment**: IntelliJ IDEA or Eclipse (recommended)

## Project Structure

```
nova-bank/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.bank/
│   │   │       ├── dao/                    # Data Access Objects (e.g., AdminDAO.java)
│   │   │       ├── model/                  # Model classes (e.g., Admin.java)
│   │   │       ├── service/                # Business logic (e.g., AdminService.java)
│   │   │       ├── gui/                    # GUI controllers (e.g., AdminDashboardController.java)
│   │   │       └── MainFX.java             # Application entry point
│   │   └── resources/
│   │       └── com.bank/
│   │           ├── login_form.fxml         # Login screen
│   │           ├── client_dashboard.fxml   # Client dashboard
│   │           ├── staff_dashboard.fxml    # Staff dashboard
│   │           ├── admin_dashboard.fxml    # Admin interface
│   │           ├── approve_loan_form.fxml  # Loan approval form
│   │           └── styles.css              # CSS for styling
│
├── Database/
│   ├── Data.sql                 # Sample data for populating tables
│   ├── Data_Schema.sql          # SQL script for creating database schema
│   ├── SQL_Queries.sql          # Collection of complex SQL queries used in the project
│
├── Final_Presentation.pdf       # Final presentation slides for the project
├── LLM_Failures.pdf             # Document highlighting failures/issues with LLMs during development
├── ProjectProposal_ERDiagram.pdf# ER diagram and project proposal documentation
├── Query_Results.pdf            # Sample output/results of SQL queries
├── Relational_Model.pdf         # Relational schema/model documentation
│
├── pom.xml                      # Maven build configuration
└── README.md                    # Project overview and documentation (this file)

```

## Installation

### Prerequisites

* Java Development Kit (JDK) 11 or higher
* Maven 3.6+
* MySQL Server 5.7 or higher
* Git (optional, for cloning the repository)

### Steps

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/your-username/nova-bank.git
   cd nova-bank
   ```

2. **Set Up the Database**:

   * Create a MySQL database named `bank`.
   * Execute the SQL scripts located in the `Database` folder to set up the tables and insert sample data.

3. **Configure Database Connection**:

   * Update the `DBUtil.java` file (located in `com.bank.util`) with your MySQL credentials:

     ```java
     public class DBUtil {
         private static final String URL = "jdbc:mysql://localhost:3306/bank";
         private static final String USER = "your_username";
         private static final String PASSWORD = "your_password";

         public static Connection getConnection() throws SQLException {
             return DriverManager.getConnection(URL, USER, PASSWORD);
         }
     }
     ```

4. **Build the Project**:

   ```bash
   mvn clean install
   ```

5. **Run the Application**:

   * Using Maven:

     ```bash
     mvn javafx:run
     ```

   * Or via IDE: Run `MainFX.java` as a Java application.

## Usage

### Login Screen (`login_form.fxml`)

* **User Authentication**:

  * **Role Selection**: Dropdown menu to select user type (Client, Staff, Admin) with default set to "Client".
  * **Credential Input**: Text field for email and password field for secure entry.
  * **Login Action**: Validates credentials using respective services and sets the current user accordingly.
  * **Error Handling**: Displays error messages with a 5-second fade-out animation if authentication fails.

* **UI/UX**:

  * Centered VBox form (400px width) with a dark theme.
  * Fade-in animation on form load and fade transition to dashboards.
  * Responsive design adjustable to window size.

* **Navigation**:

  * Directs to the respective dashboard based on role.

* **Security**:

  * Basic password input (future scope for hashing).
  * Clears other user types on login.

### Client Dashboard (`client_dashboard.fxml`)

* **Account Overview**:

  * **Account List**: Table displaying account details.
  * **Balance Summary**: Real-time total balance across all accounts.
  * **Transaction History**: View recent transactions for selected account.

* **Transaction Management**:

  * **Deposit**: Input amount and update balance.
  * **Withdrawal**: Input amount, check sufficient funds, and process withdrawal.
  * **Transfer**: Select source/destination accounts, enter amount, and execute transfer.
  * **Confirmation**: Success or error feedback with animations.

* **Loan Management**:

  * **Apply for Loan**: Select loan type, enter amount, and submit application.
  * **Loan Status**: Table of applied loans with details.
  * **Payment**: Make payments with amount input and confirmation.

* **Profile Management**:

  * **View Profile**: Display personal information in a read-only form.
  * **Edit Profile**: Editable fields with save functionality.
  * **Feedback**: Submit feedback with confirmation.

* **UI/UX**:

  * Sidebar with navigation options.
  * Centered content area with tables/forms.
  * Dark theme with hover effects on buttons.

* **Navigation**:

  * Logout returns to login screen.

### Staff Dashboard (`staff_dashboard.fxml`)

* **Account Management**:

  * **Create Account**: Input client ID, account type, initial balance, and submit.
  * **Close Account**: Select active accounts and confirm closure.
  * **Suspend Account**: Toggle account status to "Suspended" with reason input.
  * **Process Transactions**: Deposit, withdrawal, and transfer on behalf of clients.
  * **Account List**: Table of all managed accounts.

* **Loan Processing**:

  * **View Loans**: Table of assigned loans with details.
  * **Process Payment**: Input loan number and amount, update payment records.
  * **Update Status**: Mark loans as "Paid" or "Defaulted".

* **Client Management**:

  * **View Clients**: Table of all clients.
  * **Edit Client**: Update client details.
  * **Search Clients**: Filter clients by name or ID.

* **Reporting**:

  * **Generate Report**: Options for account creation, transactions, or loans.
  * **View Report**: Display report in a scrollable pane.

* **UI/UX**:

  * Sidebar with navigation options.
  * Centered content area with tables/forms.
  * Dark theme with animations for feedback.

* **Navigation**:

  * Logout returns to login screen.

### Admin Dashboard (`admin_dashboard.fxml`)

* **Sidebar Navigation**:

  * View All Staff
  * View Average Loan Amounts
  * View Staff Loan Analytics
  * View All Loans with Details
  * View Clients Without Loans
  * View Clients with Savings and Loan
  * Approve Loan
  * Logout

* **Main Content Area**:

  * **View All Staff**: Table with staff details.
  * **View Average Loan Amounts**: Table with loan type averages.
  * **View Staff Loan Analytics**: Table with staff loan data, sorted and searchable.
  * **View All Loans with Details**: Table with loan details.
  * **View Clients Without Loans**: Table with client details.
  * **View Clients with Savings and Loan**: Table with client details.
  * **Approve Loan**: Form to approve loans with feedback.

* **UI/UX**:

  * Sidebar with dark background and hover effects.
  * Centered content area with dark theme.
  * Animations for form loading and feedback.

* **Error Handling**:

  * Displays error messages in content area.
  * Logs errors via `java.util.logging`.

* **Navigation**:

  * Logout clears current admin and returns to login screen.

### Approve Loan Form (`approve_loan_form.fxml`)

* **Loan Approval**:

  * **Input Field**: Enter Loan Number.
  * **Approve Action**: Update loan status.
  * **Clear Action**: Reset form and status label.
  * **Validation**: Checks for empty input and displays error if invalid.
  * **Feedback**: Success or error messages with animations.

* **UI/UX**:

  * Centered form within the interface.
  * Dark theme with styled buttons.

* **Integration**:

  * Loaded into admin dashboard content area.

## Troubleshooting

* **FileNotFoundException**: Ensure all FXML files are in `src/main/resources/com/bank/` and included in the Maven build. Check `target/classes/com/bank/` after building.
* **Database Errors**: Verify MySQL is running and credentials in `DBUtil.java` are correct.
* **Logging**: Check console logs for SEVERE or WARNING messages for debugging.

## Contributing

1. Fork the repository.

2. Create a new branch:

   ```bash
   git checkout -b feature-branch
   ```

3. Make changes and commit
