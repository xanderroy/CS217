package uk.co.asepstrath.bank;


import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONArray;

import javax.sql.DataSource;
import java.sql.*;

import kong.unirest.core.json.JSONObject;
import org.slf4j.Logger;

public class API {

    public DataSource ds;
    public Logger log;

    public API(DataSource ds, Logger log) {
        this.ds = ds;
        this.log = log;
    }


    public void getAccounts() {
        HttpResponse<JsonNode> response = Unirest.get("https://api.asep-strath.co.uk/api/accounts").asJson();
        JSONArray arr = response.getBody().getArray();

        try (Connection connection = ds.getConnection()) {
            //
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE `Accounts` (`ID` varchar(40), `Name` varchar(40), `Balance` number, `RoundUp` boolean)");
            for (int i = 0; i < arr.length(); i++) {
                PreparedStatement ps = connection.prepareStatement("Insert INTO `Accounts` VALUES (?,?,?,?)"); //no sql injection weakspot

                JSONObject thisobj = arr.getJSONObject(i);

                String id = thisobj.getString("id");
                String name = thisobj.getString("name");
                name = name.replace("'", "''"); // '' = ' in sql
                double balance = thisobj.getDouble("startingBalance");
                boolean roundup = thisobj.getBoolean("roundUpEnabled");

                ps.setString(1, id);
                ps.setString(2, name);
                ps.setDouble(3, balance);
                ps.setBoolean(4, roundup);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Accounts Database Creation Error",e);
        }
    }

    public void getTransactions() {
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE `Transactions` (`ID` varchar(50), `Type` varchar(20), `Amount` number, `To` varchar(40), `From` varchar(40))");
        } catch (SQLException e) {
            log.error("Transaction database error", e);
        }

        for (int i = 0; i < 154; i++) { //154 is the number of pages in the transaction id
            log.info("Processing transactions page {}/153", i); //output which transactions being processed for errors
            HttpResponse<JsonNode> response = Unirest.get("https://api.asep-strath.co.uk/api/transactions?page=" + i).header("Accept", "application/json").asJson();
            //using a header requires the api to return JSON which is easier to parse
            JSONArray arr = response.getBody().getArray().getJSONObject(0).getJSONArray("results");
            //this gets the json as an array without the 'results' indentation
            try (Connection connection = ds.getConnection()) {
                for (int j = 0; j < arr.length(); j++) {
                    JSONObject obj = arr.getJSONObject(j);
                    try {
                        String id = obj.getString("id");
                        String type = obj.getString("type");
                        double amount = obj.getDouble("amount");
                        String to = null, from = null;
                        switch(type) { //switch statement allows null values to be added to the database without error, with correct fields being null.
                            case "PAYMENT", "TRANSFER":
                                to = obj.getString("to");
                                from = obj.getString("from");
                                break;
                            case "DEPOSIT":
                                to = obj.getString("to");
                                break;
                            case "WITHDRAWAL":
                                from = obj.getString("from");
                                break;
                            case "ROUNDUP":
                                to = obj.getString("to");
                            default:
                                log.info("unrecognised transaction type");
                        }

                        String query = "INSERT INTO `Transactions` VALUES (?, ?, ?, ?, ?)"; //adds transaction to db without sql injection exploit
                        PreparedStatement ps = connection.prepareStatement(query);
                        ps.setString(1, id);
                        ps.setString(2, type);
                        ps.setDouble(3, amount);
                        ps.setString(4, to);
                        ps.setString(5, from);

                        ps.executeUpdate();
                    } catch (Exception e) {
                        log.error("Exception", e);
                    }
                }
            } catch (SQLException e) {
                log.error("Transaction database error", e);
            }
        }
    }
}
