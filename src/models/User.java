package models;

import java.util.List;

public class User {
    private String name;
    private double balance; // Positive if the user is owed money, negative if the user owes money
    private Group group;

    public User(String name, Group group) {
        this.name = name;
        this.group = group;
        this.balance = 0.0;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public Group getGroup() {
        return group;
    }

    // Update the user's balance after an expense
    public void updateBalance(double amount) {
        this.balance += amount;
    }
}
