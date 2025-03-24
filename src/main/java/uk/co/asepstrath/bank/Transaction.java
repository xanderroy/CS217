package uk.co.asepstrath.bank;

public class Transaction {
    private String id;
    private String to;
    private String from;
    private String type;
    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Transaction(String id, String to, String from, String type, Double amount) {
        this.id = id;
        this.to = to;
        this.from = from;
        this.type = type;
        this.amount = amount;
    }

}
