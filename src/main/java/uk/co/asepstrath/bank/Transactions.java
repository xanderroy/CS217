package uk.co.asepstrath.bank;

import java.util.ArrayList;

public class Transactions {
    private static ArrayList<Transaction> transactions = new ArrayList<>();

    public static void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public static Transaction getTransByID(String id) {
        for (Transaction t : transactions) {
            if (t.getId()==id) {
                return t;
            }
        }
        return null;
    }
}
