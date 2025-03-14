package uk.co.asepstrath.bank;

import java.util.ArrayList;
import java.util.Objects;

public class Accounts {
    public static ArrayList<Account> accounts = new ArrayList<>();

    public static void addAccount(Account account) {
        accounts.add(account);
    }

    public static void addAccount(String id, double balance, boolean roundup, String name) {
        accounts.add(new Account(id, balance, roundup, id));
    }

    public static void transfer(String to, String from, double amount) {
        Account toAcc = new Account(), fromAcc = new Account();

        for (int i = 0; i < accounts.size(); ++i) {
            Account thisacc = accounts.get(i);
            if (Objects.equals(thisacc.getId(), to)) {
                toAcc = thisacc;
            } else if (Objects.equals(thisacc.getId(), from)) {
                fromAcc = thisacc;
            }
        }

        toAcc.deposit(amount);
        fromAcc.withdraw(amount);
    }

    public static Account getAccount(String id) {
        for (int i = 0; i < accounts.size(); ++i)  {
            if (Objects.equals(accounts.get(i).getId(), id)) {
                return accounts.get(i);
            }
        }
        return null; //if account does not exist return null.
    }
}
