package uk.co.asepstrath.bank;

import java.util.ArrayList;
import java.util.Objects;

public class Businesses {
    private static ArrayList<Business> businesses = new ArrayList<>();

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
}
