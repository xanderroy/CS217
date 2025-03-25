package uk.co.asepstrath.bank;

import java.lang.*;
import java.math.BigDecimal;

public class Account {

    private BigDecimal balance = new BigDecimal(0);
    private String name;
    private Boolean roundUp = false;
    private String id;
    private BigDecimal roundUpsPot = new BigDecimal(0);
    private String postcode;
    private String cardDetails;
    private BigDecimal totalSpending = new BigDecimal(0);

    Account() {

    }

    Account(String name) {
        this.name = name;
    }

    Account(String name, double b) {
        this.balance = new BigDecimal(b);
        this.name = name;
    }

    Account(String name, double b, boolean r, String id) {
        this.balance = new BigDecimal(b);
        this.name = name;
        this.roundUp = r;
        this.id = id;
    }

    Account(String name, double b, boolean r, String id, String postcode) {
        this.balance = new BigDecimal(b);
        this.name = name;
        this.roundUp = r;
        this.id = id;
        this.postcode = postcode;
    }

    Account(String name, double b, boolean r, String id, String postcode, String cardDetails) {
        this.balance = new BigDecimal(b);
        this.name = name;
        this.roundUp = r;
        this.id = id;
        this.postcode = postcode;
        this.cardDetails = cardDetails;
    }

    public void deposit(double amount) {
        BigDecimal toAdd = new BigDecimal(amount);
        this.balance = balance.add(toAdd);
    }

    public double getBalance() {
        return (Math.round(balance.floatValue()*100))/100.;
    }

    public void withdraw(double amount) {
        BigDecimal toSub = new BigDecimal(amount);
        this.balance = balance.subtract(toSub);
    }

    public void enableRoundUps() {
        roundUp = true;
        API.applyTransactions();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Balance: " + (Math.round(balance.floatValue() * 100)) / 100.;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public Boolean getRoundUp() {
        return roundUp;
    }

    public void setRoundUp(Boolean roundUp) {
        this.roundUp = roundUp;
        API.applyTransactions();
    }

    public double getRoundUpsPot() {
        return roundUpsPot.doubleValue();
    }

    public void setRoundUpsPot(double roundUpsPot) {
        this.roundUpsPot = (new BigDecimal(roundUpsPot));
    }

    public void addRoundUps(double amount) {
        this.roundUpsPot = roundUpsPot.add(new BigDecimal(amount));
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(String cardDetails) {
        this.cardDetails = cardDetails;
    }

    public double getTotalSpending() {
        return totalSpending.doubleValue();
    }

    public void addTotalSpending(double amount) {
        totalSpending = totalSpending.add(new BigDecimal(amount));
    }

    public void reclaimRoundUps() {
        balance = balance.add(roundUpsPot);
        roundUpsPot = roundUpsPot.subtract(roundUpsPot);
    }

}
