package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public abstract class AbstractExpense {
    private String name;
    private double amount;
    private Member payer;
    private List<Member> membersInvolved;
    private Map<Member, Double> splits; // Member -> share amount
    private LocalDate date;

    // Constructor
    public AbstractExpense(String name, double amount, Member payer, List<Member> membersInvolved, Map<Member, Double> splits, LocalDate date) {
        this.name = name;
        this.amount = amount;
        this.payer = payer;
        this.membersInvolved = membersInvolved;
        this.splits = splits;
        this.date = date;
    }

    // Abstract methods (to be implemented by subclasses)
    public abstract StringProperty getExpenseNameProperty();
    public abstract DoubleProperty getAmountProperty();
    public abstract StringProperty getMembersInvolvedProperty();
    public abstract StringProperty getDateProperty();

    // Concrete methods
    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public Member getPayer() {
        return payer;
    }

    public List<Member> getMembersInvolved() {
        return membersInvolved;
    }

    public Map<Member, Double> getSplits() {
        return splits;
    }

    public LocalDate getDate() {
        return date;
    }

    public void addSplit(Member member, double amount) {
        splits.put(member, amount);
    }
}

