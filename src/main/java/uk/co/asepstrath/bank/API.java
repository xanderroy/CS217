package uk.co.asepstrath.bank;

import com.google.gson.Gson;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONArray;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import kong.unirest.core.json.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class API {

    public DataSource ds;
    public Logger log;

    public API(DataSource ds, Logger log) {
        this.ds = ds;
        this.log = log;
    }

    public String getOauthToken() {
        String token;

        RequestBody formbody = new FormBody.Builder().add("grant_type", "client_credentials").build();

        Request req = new Request.Builder().url("https://api.asep-strath.co.uk/oauth2/token").post(formbody)
                .addHeader("Authorization", Credentials.basic("scotbank", "this1password2is3not4secure")).build();

        try (Response rep = new OkHttpClient().newCall(req).execute()) {
            String tokenjson = rep.body().string();
            String tokens[] = tokenjson.split(",");
            tokens[0] = tokens[0].replace("{\"access_token\":\"", "");
            tokens[0] = tokens[0].replace("\"", "");
            token = tokens[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return token;
    }


    public void getAccounts() {
        String token = getOauthToken();

        HttpResponse<JsonNode> response = Unirest.get("https://api.asep-strath.co.uk/api/accounts?include=postcode").
                header("Authorization", "Bearer " + token).asJson();
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
                double balance = thisobj.getDouble("startingBalance");
                boolean roundup = thisobj.getBoolean("roundUpEnabled");
                String postcode = thisobj.getString("postcode");

                Accounts.addAccount(new Account(name, balance, roundup, id, postcode)); //add the account to accounts list

                ps.setString(1, id);
                ps.setString(2, name);
                ps.setDouble(3, balance);
                ps.setBoolean(4, roundup);

                ps.executeUpdate(); //add the account into the database
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

                        Transactions.addTransaction(new Transaction(id, to, from, type, amount));

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

    public void getBusinesses() {

        HttpResponse<File> response = Unirest.get("https://api.asep-strath.co.uk/api/businesses").asFile("businesses.csv", REPLACE_EXISTING);

        File file = response.getBody();

        try(Scanner sc = new Scanner(file)) {
            while (sc.hasNext()) {
                String line = sc.nextLine();

                String tokens[] = line.split(",");

                try {
                    Businesses.addBusiness(tokens[0], tokens[1], tokens[2], (Objects.equals(tokens[3], "true")));
                } catch (IndexOutOfBoundsException e) {
                    log.error("CSV format not as expected", e);
                }
            }
        } catch (FileNotFoundException e){
            log.error("Businesses file could not be created", e);
        }

    }

    public static void applyTransactions() {
        for (Transaction t : Transactions.getAllTrans()) {
            switch(t.getType()) {
                case "DEPOSIT":
                    Accounts.getAccount(t.getTo()).deposit(t.getAmount());
                    break;
                case "WITHDRAWAL":
                    Accounts.getAccount(t.getFrom()).withdraw(t.getAmount());
                    break;
                case "TRANSFER":
                    Accounts.transfer(t.getTo(), t.getFrom(), t.getAmount());
                    break;
                case "PAYMENT":
                    if (Accounts.getAccount(t.getFrom()).getRoundUp()) {
                        Accounts.getAccount(t.getFrom()).withdraw(Math.ceil(t.getAmount()));
                        Accounts.getAccount(t.getFrom()).addRoundUps(Math.ceil(t.getAmount()) - t.getAmount());
                    }
                    else {
                        Accounts.getAccount(t.getFrom()).withdraw(t.getAmount());
                    }
                    Accounts.getAccount(t.getFrom()).addTotalSpending(t.getAmount());
                    break;
                case "ROUNDUP":
                    Accounts.getAccount(t.getTo()).deposit(Accounts.getAccount(t.getTo()).getRoundUpsPot());
                    break;
                default:
                    System.out.println("Error in processing transactions, unknown transaction type.");
            }
        }
    }
}
