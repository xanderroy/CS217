package uk.co.asepstrath.bank;

import java.lang.*;
import java.math.BigDecimal;

public class Account {

    private BigDecimal balance = new BigDecimal(0);
    private String name;

    Account() {

    }

    Account(String name) {
        this.name = name;
    }

    Account(String name, double b) {
        this.balance = new BigDecimal(b);
        this.name = name;
    }

    public void deposit(double amount) {
        BigDecimal toAdd = new BigDecimal(amount);
        balance = balance.add(toAdd);
    }

    public double getBalance() {
        double a = (Math.round(balance.floatValue()*100))/100.;
        return a;
    }

    public void withdraw(double amount) {
        BigDecimal toSub = new BigDecimal(amount);
        if (balance.subtract(toSub).floatValue() < 0) {
            throw new ArithmeticException();
        }
        balance = balance.subtract(toSub);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Balance: " + (Math.round(balance.floatValue() * 100)) / 100.;
    }
}
