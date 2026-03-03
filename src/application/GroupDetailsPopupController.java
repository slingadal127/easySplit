package application;
 
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Group;
import models.Member;
import models.MemberBalance;
import models.Expense;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
public class GroupDetailsPopupController {
 
    @FXML
    private TableView<Expense> expensesTable;  // Table to display the expenses
    @FXML
    private TableColumn<Expense, String> expenseNameColumn;
    @FXML
    private TableColumn<Expense, Double> amountColumn;
    @FXML
    private TableColumn<Expense, String> membersColumn;
    @FXML
    private TableColumn<Expense, String> dateColumn;  // New column for the date
 
    @FXML
    private TableView<MemberBalance> balancesTable;  // Table to display balances
    @FXML
    private TableColumn<MemberBalance, String> memberColumn;
    @FXML
    private TableColumn<MemberBalance, Double> balanceColumn;
    
    @FXML
    private TextArea settlementsTextArea;
 
    @FXML
    private Button closeButton;
 
    private Group group;
 
    public void initialize(Group group) {
        this.group = group;
 
        // Set up the Expenses Table
        expenseNameColumn.setCellValueFactory(cellData -> cellData.getValue().getExpenseNameProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().getAmountProperty().asObject());
        membersColumn.setCellValueFactory(cellData -> cellData.getValue().getMembersInvolvedProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());  // Binding for date
 
        // Populate the expenses table with data
        expensesTable.getItems().setAll(group.getExpenses());
 
        // Set up the Balances Table
        memberColumn.setCellValueFactory(cellData -> cellData.getValue().getMemberNameProperty());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().getBalanceProperty().asObject());
 
        // Populate balances for each member
        Map<Member, Double> balances = calculateBalances();
        balancesTable.getItems().clear();  // Clear previous data
 
        for (Map.Entry<Member, Double> entry : balances.entrySet()) {
            balancesTable.getItems().add(new MemberBalance(entry.getKey(), entry.getValue()));
        }
        
        // Add logic to settle the balances
       //<String> settlements = settleBalances(balances);
        displaySettlements(balances);
    }
 
    private Map<Member, Double> calculateBalances() {
        Map<Member, Double> balances = new HashMap<>();
        
        // Initialize all members with 0 balance
        for (Member member : group.getMembers()) {
            balances.put(member, 0.0);
        }
 
     // Calculate the balances for each expense
        for (Expense expense : group.getExpenses()) {
            double totalAmount = expense.getAmount();
            int numPeople = group.getMembers().size();
            double share = totalAmount / numPeople;
 
            Member payer = expense.getPayer();
 
            // Update the payer's balance (they've paid for the others)
            balances.put(payer, balances.get(payer) + (totalAmount - share));
 
            // Update the balances for the other members who owe money to the payer
            for (Member member : group.getMembers()) {
                if (!member.equals(payer)) {
                    balances.put(member, balances.get(member) - share);
                }
            }
        }
 
        return balances;
    }
 
    
 
    
    // Display the settlements in the TextArea
    private void displaySettlements(Map<Member, Double> balances) {
        StringBuilder sb = new StringBuilder("Settlements:\n");
 
        // Iterate through the balances to build the settlement information
        for (Map.Entry<Member, Double> entry : balances.entrySet()) {
            Member member = entry.getKey();
            double balance = entry.getValue();
 
            if (balance < 0) {
                sb.append(member.getName()).append(" owes ").append(Math.abs(balance)).append("\n");
            } else if (balance > 0) {
                sb.append(member.getName()).append(" should receive ").append(balance).append("\n");
            }
        }
 
        // Set the TextArea text
        settlementsTextArea.setText(sb.toString());
    }
 
 
    // Close the window after displaying the group details
    @FXML
    public void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}