package application;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import utils.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddExpenseController {

    @FXML
    private TextField amountField; // Field for entering expense amount

    @FXML
    private ComboBox<String> paidByDropdown; // Dropdown to select payer

    @FXML
    private ComboBox<String> splitTypeDropdown; // Dropdown for split type (e.g., equal, custom)

    @FXML
    private void handleSubmitExpense() {
        // Step 1: Validate user inputs
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered. Please enter a numeric value.");
            return;
        }

        String paidBy = paidByDropdown.getValue();
        String splitType = splitTypeDropdown.getValue();

        if (paidBy == null || splitType == null) {
            System.out.println("Please select both 'Paid By' and 'Split Type' values.");
            return;
        }

        // Assuming the GroupID and PayerID are dynamically fetched
        int groupID = 1; // Replace with the actual GroupID from the application logic
        int payerID = Integer.parseInt(paidBy); // Replace with actual PayerID from the dropdown value

        // Step 2: Insert the expense into the database
        String insertExpenseSQL = "INSERT INTO Expenses (GroupID, ExpenseName, Amount, PayerID, ExpenseDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = SQLConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertExpenseSQL)) {

            stmt.setInt(1, groupID); // Replace with actual GroupID
            stmt.setString(2, "New Expense"); // Replace with actual expense name
            stmt.setDouble(3, amount); // Set the amount entered by the user
            stmt.setInt(4, payerID); // Replace with actual PayerID
            stmt.setDate(5, new java.sql.Date(System.currentTimeMillis())); // Current date or a date from the UI

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Expense added successfully!");
            } else {
                System.out.println("Expense not added. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving expense to database: " + e.getMessage());
        }
    }
}

