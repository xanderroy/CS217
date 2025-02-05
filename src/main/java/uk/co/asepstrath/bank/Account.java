package uk.co.asepstrath.bank;

import java.lang.*;
import java.math.BigDecimal;

public class Account {

    private BigDecimal balance = new BigDecimal(0);

    public void deposit(int amount) {
        BigDecimal toAdd = new BigDecimal(amount);
        balance.add(toAdd);
    }

    public float getBalance() {
        return balance.floatValue();
    }

    public void withdraw(int amount) {
        BigDecimal toSub = new BigDecimal(amount);
        if (balance.subtract(toSub).floatValue() < 0) {
            throw new ArithmeticException();
        }

        balance.subtract(toSub);
    }



}
