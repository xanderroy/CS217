package uk.co.asepstrath.bank;

import java.util.ArrayList;
import java.util.Objects;

public class Transactions {
    private static ArrayList<Transaction> transactions = new ArrayList<>();

    public static void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public static Transaction getTransByID(String id) {
        for (Transaction t : transactions) {
            if (Objects.equals(t.getId(), id)) {
                return t;
            }
        }
        return null;
    }

    public static ArrayList<Transaction> getAllTrans() {
        return transactions;
    }
}
