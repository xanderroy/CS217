package uk.co.asepstrath.bank;

import java.lang.*;
import java.math.BigDecimal;

public class Account {

    private BigDecimal balance = new BigDecimal(0);

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



}
