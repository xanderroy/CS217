package uk.co.asepstrath.bank;

public class Business {
    private String id;
    private String name;
    private String category;
    private boolean sanctioned;


    public Business(String id, String name, String category, boolean sanctioned) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.sanctioned = sanctioned;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSanctioned() {
        return sanctioned;
    }

    public void setSanctioned(boolean sanctioned) {
        this.sanctioned = sanctioned;
    }
}
