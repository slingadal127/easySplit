package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Group;
import models.Member;

public class EditGroupPopupController {

    @FXML
    private TextField groupNameField;
    
    @FXML
    private VBox membersVBox;
    
    
    @FXML
    private TextField memberNameField;
    
    @FXML
    private TextField emailField;
    


    private Group group;

    public void initialize(Group group) {
        this.group = group;
        groupNameField.setText(group.getGroupName());
        
     // Populate members in VBox
        for (Member member : group.getMembers()) {
            addMemberToVBox(member);
        }
    }
    
    // Add member to VBox dynamically
    private void addMemberToVBox(Member member) {
        HBox memberBox = new HBox(10); // Spacing of 10px between elements

        // Member's editable fields
        TextField memberNameField = new TextField(member.getName());
        TextField emailField = new TextField(member.getEmail());
        
        HBox buttonContent = new HBox(5);  // 5px space between elements

     // Create the Label
     Label label = new Label("Edit");

        // Edit Button for member
        Button editButton = new Button("Reset");
        editButton.setOnAction(event -> handleEditMember(member, memberNameField, emailField));

        buttonContent.getChildren().add(label);
        editButton.setGraphic(buttonContent);
        
        // Delete Button for member
        Button deleteButton = new Button("Delete");
        deleteButton.setText("Delete Member");
        deleteButton.setOnAction(event -> handleDeleteMember(member));

        // Add the member details and buttons to the HBox
        memberBox.getChildren().addAll(memberNameField, emailField, editButton, deleteButton);

        // Add HBox to VBox
        membersVBox.getChildren().add(memberBox);
    }

    // Handle adding a new member
    @FXML
    private void handleAddMember() {
    	String name = memberNameField.getText();
        String email = emailField.getText();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Member");
        dialog.setHeaderText("Enter the name of the new member:");
        
        // Validate input
        if (name.isEmpty() || email.isEmpty() ) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields must be filled!");
            return;
        }
        


        Member newMember = new Member(name, email);
        group.addMember(newMember);
        addMemberToVBox(newMember);
        
        // Clear input fields after adding
        memberNameField.clear();
        emailField.clear();
    }

    
    private void handleEditMember(Member member, TextField nameField, TextField emailField) {
        String newName = nameField.getText();
        String newEmail = emailField.getText();
        

        // Validate email
        if (newEmail.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Email cannot be empty!");
            return;
        }

        // Update member's properties
        member.setName(newName);
        member.setEmail(newEmail);

        // Refresh the VBox to reflect the changes
        membersVBox.getChildren().clear();
        for (Member m : group.getMembers()) {
            addMemberToVBox(m);
        }
    }
    
    private void handleDeleteMember(Member member) {
        // Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Member");
        alert.setHeaderText("Are you sure you want to delete this member?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            group.removeMember(member);
            membersVBox.getChildren().removeIf(node -> {
                HBox memberBox = (HBox) node;
                return memberBox.getChildren().stream().anyMatch(child -> child instanceof TextField && ((TextField) child).getText().equals(member.getName()));
            });
        }
    }

    @FXML
    private void handleSave() {String newGroupName = groupNameField.getText();
    if (newGroupName != null && !newGroupName.isEmpty()) {
        group.setGroupName(newGroupName);
        
        // Show success alert
        showAlert(Alert.AlertType.INFORMATION, "Saved Successfully", "The group details have been saved.");
        
        // Close the window (current stage)
        Stage stage = (Stage) groupNameField.getScene().getWindow();
        stage.close();
    } else {
        // Show error if the group name is empty
        showAlert(Alert.AlertType.ERROR, "Input Error", "Group name cannot be empty!");
    }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
