package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Group;
import utils.SQLConnection;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML
    private Button addGroupButton;

    @FXML
    private VBox groupVBox; // VBox to hold the groups, no longer static

    private List<Group> groups = new ArrayList<>(); // Initialize groups list
    
    private int currentUserId;

    @FXML
    private void initialize() {
        // Initialize the dashboard with the existing groups (if any)
        if (groups != null) {
            for (Group group : groups) {
                addGroupToDashboard(group);
            }
        }
    }

    @FXML
    private void handleAddGroup() {
        try {
            // Load the Add Group Popup FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/addGroup.fxml"));
            Parent root = loader.load();

            // Show the popup in a new window
            Stage stage = new Stage();
            stage.setTitle("Add Group");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make the window modal (blocking)
            stage.showAndWait();

            AddGroupPopupController popupController = loader.getController();
            Group newGroup = popupController.handleSubmitGroup(); // Assume the popup controller returns a new group
            if (newGroup != null) {
                saveGroupToDatabase(newGroup); // Save the group and its members to the database
                groups.add(newGroup); // Add to the list
                addGroupToDashboard(newGroup); // Dynamically add the group to the dashboard
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create a custom HBox for each group and add it to the VBox
    private void addGroupToDashboard(Group group) {
        // Create a new HBox to represent the group
        HBox groupBox = new HBox(10); // 10px spacing between elements

        // Add the group's name
        Text groupNameText = new Text(group.getGroupName());

        // Add a button to manage the group (e.g., Edit, Delete, etc.)
        Button manageButton = new Button("Add Expense");
        manageButton.setOnAction(event -> {
            openAddExpensePopup(group);
        });

        // Add a button to view group details (expenses and balances)
        Button viewDetailsButton = new Button("View Group Details");
        viewDetailsButton.setOnAction(event -> openGroupDetailsPopup(group));

        // Add an Edit button to modify the group
        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> openEditGroupPopup(group, groupBox));

        // Add a Delete button to remove the group
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> deleteGroup(group));

        // Add the text and button to the HBox
        if (groupNameText != null) {
            groupBox.getChildren().addAll(groupNameText, manageButton, viewDetailsButton, editButton, deleteButton);

            // Add the HBox to the VBox
            groupVBox.getChildren().add(groupBox);
        }
    }
    
    
    @FXML
    private void handleLogout() {
        try {
            // Load the login page FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login.fxml"));
            Parent loginRoot = loader.load();

            // Get the current stage (window) and set the scene to the login page
            Stage stage = (Stage) addGroupButton.getScene().getWindow();
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("Easy Split - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to load the login page.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openEditGroupPopup(Group group, HBox groupBox) {
        try {
            // Load the Edit Group Popup FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/editGroup.fxml"));
            Parent root = loader.load();

            // Get the controller for the Edit Group form
            EditGroupPopupController popupController = loader.getController();
            popupController.initialize(group); // Pass the group to the popup

            // Show the popup in a new window
            Stage stage = new Stage();
            stage.setTitle("Edit Group");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            stage.showAndWait();

            // After the popup is closed, refresh the dashboard if the group was modified
            for (Node node : groupBox.getChildren()) {
                if (node instanceof Text) {
                    ((Text) node).setText(group.getGroupName()); 
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteGroup(Group group) {
        // Show a confirmation dialog
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Group");
        alert.setHeaderText("Are you sure you want to delete this group?");
        alert.setContentText("This action cannot be undone.");

        // If the user confirms, remove the group
        if (alert.showAndWait().get() == ButtonType.OK) {
            groups.remove(group); // Remove from the list
            groupVBox.getChildren().clear(); // Clear the current UI
            initialize(); // Refresh the dashboard
        }
    }

    private void openAddExpensePopup(Group group) {
        try {
            // Load the Add Expense Popup FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/expenseForm.fxml"));
            Parent root = loader.load();

            // Get the controller for the Add Expense form
            AddExpensePopupController popupController = loader.getController();
            popupController.initialize(group);

            // Show the popup in a new window
            Stage stage = new Stage();
            stage.setTitle("Add Expense");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Open Group Details Popup
    private void openGroupDetailsPopup(Group group) {
        try {
            // Load the Group Details Popup FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/groupDetailsPopup.fxml"));
            Parent root = loader.load();

            // Get the controller for the Group Details Popup
            GroupDetailsPopupController popupController = loader.getController();
            popupController.initialize(group);

            // Show the popup in a new window
            Stage stage = new Stage();
            stage.setTitle("Group Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save group and members to the database
    private void saveGroupToDatabase(Group group) {
        String groupQuery = "INSERT INTO Groups (GroupName) OUTPUT INSERTED.GroupID VALUES (?)";
        String memberQuery = "INSERT INTO Members (GroupID, Name, Email) VALUES (?, ?, ?)";

        try (Connection connection = SQLConnection.getConnection();
             PreparedStatement groupStmt = connection.prepareStatement(groupQuery);
             PreparedStatement memberStmt = connection.prepareStatement(memberQuery)) {

            // Save group
            groupStmt.setString(1, group.getGroupName());
            ResultSet groupResult = groupStmt.executeQuery();
            int groupId = 0;
            if (groupResult.next()) {
                groupId = groupResult.getInt("GroupID");
                group.setGroupID(groupId); // Set the generated GroupID in the group object
            }

            // Save members
            for (var member : group.getMembers()) {
                memberStmt.setInt(1, groupId); // Use the generated GroupID
                memberStmt.setString(2, member.getName());
                memberStmt.setString(3, member.getEmail());
                memberStmt.addBatch(); // Add to batch for execution
            }
            memberStmt.executeBatch(); // Execute all member insertions in one go

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to save group and members to the database.");
        }
    }


	public void setCurrentUserId(int userId) {
		// TODO Auto-generated method stub
		this.currentUserId=userId;
		
	}

	
}
