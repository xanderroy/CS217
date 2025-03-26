package uk.co.asepstrath.bank;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Businesses {
    public static ArrayList<Business> businesses = new ArrayList<>();

    public static void addBusiness(Business b) {
        businesses.add(b);
    }

    public static void addBusiness(String id, String name, String cat, boolean sanctioned) {
        businesses.add(new Business(id, name, cat, sanctioned));
    }

    public static Business getBusinessByID(String id) {
        for (int i = 0; i < businesses.size(); i++) {
            if (Objects.equals(businesses.get(i).getId(), id)) {
                return businesses.get(i);
            }
        }

        return null;
    }

    public static ArrayList<ArrayList<String>> sanctionedBusinesses() {
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (Transaction t : Transactions.getAllTrans()) {
            if (t.getType().equals("PAYMENT")) {
                if (Businesses.getBusinessByID(t.getTo()).isSanctioned()) {
                    list.add(Transactions.getTransDetails(t.getId()));
                }
            }
        }
        return list;
    }

    public static HashMap<String, Double> sanctionedTotals(){
        HashMap<String, Double> totals = new HashMap<>();

        var sanctionedtrans = Businesses.sanctionedBusinesses();

        for (var t : sanctionedtrans) {
            String id = t.get(1); //business id
            String strbalance = t.get(4); //balance
            double balance = Double.parseDouble(strbalance);

            if (!totals.containsKey(id)) {
                totals.put(id, balance);
            }
            else {
                totals.put(id, totals.get(id) + balance);
            }
        }

        return totals;
    }
}
