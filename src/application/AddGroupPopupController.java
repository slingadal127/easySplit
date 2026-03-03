package application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import models.Group;
import models.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddGroupPopupController {

    @FXML
    private TextField groupNameField;

    @FXML
    private VBox membersVBox;

    private List<Member> members;

    @FXML
    private Button addMemberButton;

    @FXML
    private Button saveGroupButton;

    private ObservableList<Group> groups;

    // Initialize the controller
    @FXML
    private void initialize() {
        members = new ArrayList<>();
    }

    // No-argument constructor (this is required by JavaFX FXML loader)
    public AddGroupPopupController() {
        // You can leave this empty if you don't need any special setup
    }

    // Handles adding a member to the group
    @FXML
    private void handleAddMember() {
        HBox memberHBox = new HBox(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(event -> removeMember(nameField, emailField));

        memberHBox.getChildren().addAll(nameField, emailField, removeButton);
        membersVBox.getChildren().add(memberHBox);
    }

    // Removes a member (HBox) from the list
    private void removeMember(TextField nameField, TextField emailField) {
        membersVBox.getChildren().remove(nameField.getParent());  // Remove the HBox containing the member
    }

    // Handles the submission of the form
    @FXML
    public Group handleSubmitGroup() {
        String groupName = groupNameField.getText();
        Group newGroup = new Group(groupName);

        if (groupName.isEmpty()) {
            showAlert("Error", "Group name cannot be empty.", AlertType.ERROR);
            return null;
        }

        if (membersVBox == null || membersVBox.getChildren().isEmpty()) {
            showAlert("Error", "At least one member must be added.", AlertType.ERROR);
            return null;
        }

        // Add members to the list (collecting name and email)
        for (int i = 0; i < membersVBox.getChildren().size(); i++) {
            HBox memberHBox = (HBox) membersVBox.getChildren().get(i);
            TextField nameField = (TextField) memberHBox.getChildren().get(0);
            TextField emailField = (TextField) memberHBox.getChildren().get(1);

            String name = nameField.getText();
            String email = emailField.getText();

            // Validate the member details
            if (name.isEmpty() || email.isEmpty()) {
                showAlert("Error", "All member fields must be filled.", AlertType.ERROR);
                return null;
            }

            if (!isValidEmail(email)) {
                showAlert("Error", "Please enter a valid email address.", AlertType.ERROR);
                return null;
            }

            members.add(new Member(name, email));
            newGroup.addMember(new Member(name, email));
        }

        // Proceed with further action, like saving the group and members or navigating
        // In this example, we simply show a success message        
        // Close the window
        closeWindow();
        return newGroup;
    }

    // Email validation using regular expression
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Handles the cancel action (clear the form and close the window)
    @FXML
    private void handleCancelGroup() {
        // Clear the form fields
        groupNameField.clear();
        membersVBox.getChildren().clear();

        // Close the window
        closeWindow();
    }

    // Helper method to show alerts
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Closes the current window
    private void closeWindow() {
        Stage stage = (Stage) groupNameField.getScene().getWindow();
        stage.close();
    }
}
