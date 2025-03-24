package uk.co.asepstrath.bank;

import java.lang.reflect.Array;
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

    public static ArrayList<String> getTransDetails(String id){
        ArrayList<String> details = new ArrayList<>();
        details.add(getTransByID(id).getId());
        details.add(getTransByID(id).getTo());
        details.add(getTransByID(id).getFrom());
        details.add(getTransByID(id).getType());
        details.add(String.valueOf(getTransByID(id).getAmount()));

        return details;
    }

}
