package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.Group;
import models.Expense;
import models.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AddExpensePopupController {

    @FXML
    private TextField expenseNameField; // Text field for the expense name

    @FXML
    private ComboBox<Member> payerComboBox; // ComboBox for selecting the payer

    @FXML
    private DatePicker expenseDatePicker; // Date picker for the expense date

    @FXML
    private TextField amountField; // Text field for the amount of money paid

    @FXML
    private VBox membersVBox; // VBox for displaying members with checkboxes

    @FXML
    private Button submitButton; // Button for submitting the expense
    
    @FXML
    private ComboBox<String> splitMethodComboBox; // ComboBox for selecting the split method (e.g., "Equal", "Percentage", "Custom")

    @FXML
    private VBox splitDetailsVBox; // VBox to dynamically show input fields based on split method (percentages or custom amounts)

    private Group group; // The selected group where the expense will be added

    // Initialize the controller with the selected group
    public void initialize(Group group) {
        this.group = group;
        
        // Populate splitMethodComboBox
        splitMethodComboBox.getItems().addAll("Equal", "By Percentage", "Custom Amounts");
        splitMethodComboBox.setOnAction(event -> updateSplitDetails());
        
        payerComboBox.getItems().clear();  // Clear any existing items
        payerComboBox.getItems().addAll(group.getMembers());

        // Set a custom StringConverter to display the member names in the ComboBox
        payerComboBox.setConverter(new StringConverter<Member>() {
            @Override
            public String toString(Member member) {
                return member != null ? member.getName() : "";  // Display the name of the member
            }

            @Override
            public Member fromString(String string) {
                return null;  // We don't need this, as the ComboBox already stores the member object
            }
        });

        // Add checkboxes for each group member to the Split By section
        for (Member member : group.getMembers()) {
            HBox memberHBox = new HBox(10);
            CheckBox memberCheckBox = new CheckBox(member.getName());
            memberCheckBox.setUserData(member); // Store member object in the checkbox

            // Ensure that the payer can't be selected as part of the split
            if (payerComboBox.getValue() != null && payerComboBox.getValue().equals(member)) {
                memberCheckBox.setDisable(true);
            }

            memberHBox.getChildren().add(memberCheckBox);
            membersVBox.getChildren().add(memberHBox);
        }

        // Set default selection for splitMethodComboBox (optional)
        splitMethodComboBox.getSelectionModel().select(0); // "Equally" by default
        updateSplitDetails(); // Initialize splitDetailsVBox based on the default method
    }

    // Update the UI based on the selected split method
    private void updateSplitDetails() {
        String splitMethod = splitMethodComboBox.getValue();
        splitDetailsVBox.getChildren().clear(); // Clear existing inputs

        if ("Equal".equals(splitMethod)) {
            Label equallyLabel = new Label("Expense will be split equally among members.");
            splitDetailsVBox.getChildren().add(equallyLabel);
        } else if ("By Percentage".equals(splitMethod)) {
            // Show fields for entering percentage splits for each member
            for (Member member : getSelectedMembersForSplit()) {
                HBox hbox = new HBox(10);
                TextField percentageField = new TextField();
                percentageField.setPromptText("Percentage for " + member.getName() + " (%)");
                hbox.getChildren().add(percentageField);
                splitDetailsVBox.getChildren().add(hbox);
            }
        } else if ("Custom Amounts".equals(splitMethod)) {
            // Show fields for entering custom amounts for each member
            for (Member member : getSelectedMembersForSplit()) {
                HBox hbox = new HBox(10);
                TextField amountField = new TextField();
                amountField.setPromptText("Amount for " + member.getName());
                hbox.getChildren().add(amountField);
                splitDetailsVBox.getChildren().add(hbox);
            }
        }
    }

    // Handle the submit expense action
    @FXML
    public void handleSubmitExpense() {
        String expenseName = expenseNameField.getText();
        Member payer = payerComboBox.getValue();
        String amountText = amountField.getText();
        LocalDate dateText = expenseDatePicker.getValue() != null ? expenseDatePicker.getValue() : null;
        List<Member> membersInvolved = getSelectedMembersForSplit();

        // Validate the form fields
        if (expenseName.isEmpty()) {
            showAlert("Error", "Expense name cannot be empty.", AlertType.ERROR);
            return;
        }

        if (payer == null) {
            showAlert("Error", "Please select who paid for the expense.", AlertType.ERROR);
            return;
        }

        if (amountText.isEmpty()) {
            showAlert("Error", "Amount cannot be empty.", AlertType.ERROR);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert("Error", "Amount must be greater than zero.", AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount.", AlertType.ERROR);
            return;
        }

        if (dateText == null) {
            showAlert("Error", "Please select a date.", AlertType.ERROR);
            return;
        }

        // Collect selected members to split the cost
        List<Member> membersToSplit = getSelectedMembersForSplit();

        if (membersToSplit.isEmpty()) {
            showAlert("Error", "Please select members to split the expense with.", AlertType.ERROR);
            return;
        }

        // Split the expense based on the selected method
        Map<Member, Double> splits = new HashMap<>();

        String splitMethod = splitMethodComboBox.getValue();
        switch (splitMethod) {
            case "Equal":
                splits = splitEqually(amount, membersToSplit);
                if (splits == null) {
                    return; // Invalid split (amount is not divisible evenly)
                }
                break;
            case "By Percentage":
                splits = splitByPercentage(amount, membersToSplit);
                if (splits.isEmpty()) {
                    return; // Invalid percentage split
                }
                break;
            case "Custom Amounts":
                splits = splitByCustomAmount(amount, membersToSplit);
                if (splits.isEmpty()) {
                    return; // Invalid custom amount split
                }
                break;
        }

        // Create a new expense object using the constructor
        Expense expense = new Expense(expenseName, amount, payer, membersInvolved, splits, dateText);

        // Add the expense to the group
        group.addExpense(expense);

        // Show a success message and close the popup
        showAlert("Success", "Expense added successfully!", AlertType.INFORMATION);
        closeWindow();
    }

    // Split the expense equally among all members
    private Map<Member, Double> splitEqually(double amount, List<Member> membersToSplit) {
        Map<Member, Double> splits = new HashMap<>();
        double equalShare = amount / membersToSplit.size();

        for (Member member : membersToSplit) {
            splits.put(member, equalShare);
        }

        return splits;
    }

    // Split the expense based on percentage
    private Map<Member, Double> splitByPercentage(double amount, List<Member> membersToSplit) {
        Map<Member, Double> splits = new HashMap<>();
        double totalPercentage = 0;

        // Gather percentages from the UI inputs
        List<Double> percentages = new ArrayList<>();
        for (Node node : splitDetailsVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (Node child : hbox.getChildren()) {
                    if (child instanceof TextField) {
                        try {
                            double percentage = Double.parseDouble(((TextField) child).getText());
                            percentages.add(percentage);
                            totalPercentage += percentage;
                        } catch (NumberFormatException e) {
                            showAlert("Error", "Invalid percentage entered.", AlertType.ERROR);
                            return splits;
                        }
                    }
                }
            }
        }

        // Ensure that the total percentage adds up to 100%
        if (totalPercentage != 100) {
            showAlert("Error", "Total percentage must add up to 100%.", AlertType.ERROR);
            return splits;
        }

        // Calculate the amounts for each member based on the percentages
        int i = 0;
        for (Member member : membersToSplit) {
            double percentage = percentages.get(i++);
            double memberAmount = (percentage / 100) * amount;
            splits.put(member, memberAmount);
        }

        return splits;
    }

    // Split the expense by custom amounts
    private Map<Member, Double> splitByCustomAmount(double amount, List<Member> membersToSplit) {
        Map<Member, Double> splits = new HashMap<>();
        double totalCustomAmount = 0;

        // Gather custom amounts from the UI inputs
        List<Double> customAmounts = new ArrayList<>();
        for (Node node : splitDetailsVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (Node child : hbox.getChildren()) {
                    if (child instanceof TextField) {
                        try {
                            double customAmount = Double.parseDouble(((TextField) child).getText());
                            customAmounts.add(customAmount);
                            totalCustomAmount += customAmount;
                        } catch (NumberFormatException e) {
                            showAlert("Error", "Invalid custom amount entered.", AlertType.ERROR);
                            return splits;
                        }
                    }
                }
            }
        }

        // Ensure that the total custom amounts add up to the total expense amount
        if (totalCustomAmount != amount) {
            showAlert("Error", "Total custom amounts must equal the expense amount.", AlertType.ERROR);
            return splits;
        }

        // Assign the custom amounts to each member
        int i = 0;
        for (Member member : membersToSplit) {
            double customAmount = customAmounts.get(i++);
            splits.put(member, customAmount);
        }

        return splits;
    }

    // Display an alert with a specific message
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Close the popup window after submission
    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    // Get selected members for splitting the expense
    private List<Member> getSelectedMembersForSplit() {
        List<Member> selectedMembers = new ArrayList<>();
        for (Node node : membersVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (Node child : hbox.getChildren()) {
                    if (child instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) child;
                        if (checkBox.isSelected()) {
                            selectedMembers.add((Member) checkBox.getUserData());
                        }
                    }
                }
            }
        }
        return selectedMembers;
    }
}
