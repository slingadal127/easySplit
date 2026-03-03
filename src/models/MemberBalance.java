package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

public class MemberBalance {

    private Member member;
    private Double balance;


    public MemberBalance(Member member, Double balance) {
        this.member = member;
        this.balance = balance;
        
    }

    public StringProperty getMemberNameProperty() {
        return new SimpleStringProperty(member.getName());
    }

    public DoubleProperty getBalanceProperty() {
        return new SimpleDoubleProperty(balance);
    }
    
    public Member getMember() {
        return member;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}