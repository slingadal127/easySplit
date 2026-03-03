package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Expense extends AbstractExpense{
    private String name;
    private double amount;
    private Member payer;
    private List<Member> membersInvolved;
    private Map<Member, Double> splits; // Member -> share amount
    private LocalDate date;
    
    // Constructor, getters, and setters
    public Expense(String name, double amount, Member payer, List<Member> membersInvolved, Map<Member, Double> splits, LocalDate date) {
    	 super(name, amount, payer, membersInvolved, splits, date);
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public List<Member> getMembersInvolved() {
        return membersInvolved;
    }

    public Map<Member, Double> getSplits() {
        return splits;
    }

    public Member getPayer() {
        return payer;
    }
    
    public LocalDate getDate() {
        return date;
    }

    // For TableView binding, using SimpleStringProperty and SimpleDoubleProperty
    public StringProperty getExpenseNameProperty() {
        return new SimpleStringProperty(name);
    }

    public DoubleProperty getAmountProperty() {
        return new SimpleDoubleProperty(amount);
    }

    public StringProperty getMembersInvolvedProperty() {
        StringBuilder members = new StringBuilder();
        for (Member member : membersInvolved) {
            if (members.length() > 0) {
                members.append(", ");
            }
            members.append(member.getName());
        }
        return new SimpleStringProperty(members.toString());
    }
    
    // Date property for TableView (Formatted)
    public StringProperty getDateProperty() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");  // Format date as string
        return new SimpleStringProperty(date.format(formatter));
    }
    
    public void addSplit(Member member, double amount) {
        splits.put(member, amount);
    }
}
