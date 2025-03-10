package uk.co.asepstrath.bank;

import com.google.gson.JsonNull;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONArray;

import javax.sql.DataSource;
import java.beans.ExceptionListener;
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
                String id = arr.getJSONObject(i).getString("id");
                String name = arr.getJSONObject(i).getString("name");
                name = name.replace("'", "''");
                double balance = arr.getJSONObject(i).getDouble("startingBalance");
                boolean roundup = arr.getJSONObject(i).getBoolean("roundUpEnabled");
                stmt.executeUpdate("INSERT INTO Accounts VALUES ('" + id + "', '"+ name + "', '" + balance + "', '" + roundup + "')");
            }
        } catch (SQLException e) {
            log.error("Accounts Database Creation Error",e);
        }
    }

    public void getTransactions() {
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE `Transactions` (`ID` varchar(50), `Type` varchar(20), `Amount` number, `To` varchar(10), `From` varchar(40))");
        } catch (SQLException e) {
            log.error("Transaction database error", e);
        }

        for (int i = 0; i < 154; i++) { //154 is the number of pages in the transaction id
            log.info("Processing transactions page {}/153", i);
            HttpResponse<JsonNode> response = Unirest.get("https://api.asep-strath.co.uk/api/transactions?page=" + i).header("Accept", "application/json").asJson();
            JSONArray arr = response.getBody().getArray().getJSONObject(0).getJSONArray("results");
            try (Connection connection = ds.getConnection()) {
                Statement stmt = connection.createStatement();
                for (int j = 0; j < arr.length(); j++) {
                    JSONObject obj = arr.getJSONObject(j);
                    try {
                        String id = obj.getString("id");
                        String type = obj.getString("type");
                        double amount = obj.getDouble("amount");
                        String to = obj.getString("to");
                        String from = obj.getString("from");

                        String query = "INSERT INTO `Transactions` VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement ps = connection.prepareStatement(query);
                        ps.setString(1, id);
                        ps.setString(2, type);
                        ps.setDouble(3, amount);
                        ps.setString(4, to);
                        ps.setString(5, from);

                        ps.executeUpdate();
                    } catch (Exception e) {
                        continue;
                    }
                }
                Statement test = connection.createStatement();
            } catch (SQLException e) {
                log.error("Transaction database error", e);
            }
        }
    }
}
