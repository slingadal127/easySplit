package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.Group;
import models.Member;

public class GroupBalanceController {

    @FXML
    private VBox balanceVBox; // VBox to hold member balance labels

    private Group group; // Reference to the group

    // Initialize the controller with the group
    public void initialize(Group group) {
        this.group = group;
        displayBalances();
    }

    // Method to display the balances for all members in the group
    private void displayBalances() {
        balanceVBox.getChildren().clear();  // Clear existing labels

        for (Member member : group.getMembers()) {
            double balance = group.getBalance(member);
            Label balanceLabel = new Label(member.getName() + ": $" + String.format("%.2f", balance));
            balanceVBox.getChildren().add(balanceLabel);
        }
    }

    // Method to refresh the displayed balances
    @FXML
    public void refreshBalances() {
        displayBalances();
    }
}

