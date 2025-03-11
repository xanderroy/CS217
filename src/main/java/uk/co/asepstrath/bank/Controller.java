package uk.co.asepstrath.bank;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Base64;
import java.net.http.HttpRequest;
import java.net.URI;
import java.util.*;

@Path("/bank")
public class Controller {
    private final DataSource dataSource;
    private final Logger logger;


    public Controller(DataSource ds, Logger log) {
        dataSource = ds;
        logger = log;
    }

    @GET("/accounts")
    public ModelAndView showAccounts() {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Accounts"); //fetch all accounts from list

            ArrayList<Map<String, Object>> accounts = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> account = new HashMap<>();
                account.put("id", resultSet.getString("ID"));
                account.put("name", resultSet.getString("Name"));
                account.put("balance", resultSet.getDouble("Balance"));
                account.put("roundup", resultSet.getBoolean("RoundUp"));
                accounts.add(account);
            }

            Map<String, Object> model = new HashMap<>();
            model.put("accounts", accounts);

            return new ModelAndView("accounts.hbs", model);
        } catch (SQLException e) {
            logger.error("Error fetching accounts", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Error fetching accounts");
        }
    }

    @GET("/{id}")
    public ModelAndView UserDetails(Context ctx) {
        String userId = ctx.path("id").value(); //sets the id from url as a string
        Map<String, Object> model = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM Accounts WHERE ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) { //using prepared statement to make sure the datatype it expects is correct
                statement.setString(1, userId); //puts the id into the query
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) { //checks if there's an account with the matching id
                    model.put("id", resultSet.getString("ID"));
                    model.put("name", resultSet.getString("Name"));
                    model.put("balance", resultSet.getDouble("Balance"));
                    model.put("roundup", resultSet.getBoolean("RoundUp"));
                } else {
                    model.put("error", "User not found"); //returns error statement when no matching account is detected
                }
            }

        } catch (SQLException e) {
            logger.error("Database Error Occurred", e);
            // If something does go wrong this will log the stack trace
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
            // And return a HTTP 500 error to the requester
        }
        return new ModelAndView("userDetails.hbs", model);
    }

    @GET("/{id}/transactions")
    public ArrayList<String> AllTransAcc(Context ctx) {
        String accountid = ctx.path("id").value();
        ArrayList<String> trans = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = ("SELECT * FROM `Transactions` WHERE `To` = ? OR `From` = ?"); //transaction is relevant to account if to or from is that account
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountid);
            ps.setString(2, accountid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("ID");
                double amount = rs.getDouble("Amount");
                String type = rs.getString("Type");
                String to = rs.getString("To");
                String from = rs.getString("From");
                trans.add("ID: " + id + ", amount: " + amount + ", type: " + type + ", to: " + to + ", from:" + from); //returns an absolute mess sorry haroon
            }
        } catch (Exception e) {
            logger.error("Error in database", e);
        }
        return trans;
    }

    @GET("/login")
    public void loginSite(@QueryParam("userId") String userId, Context ctx) {
        Boolean idfound = false;
        List idList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT ID FROM Accounts"; //Just getting the ID's
            PreparedStatement statement = connection.prepareStatement(query); //Preparing the query
            ResultSet resultSet = statement.executeQuery(); //Executing
            while (resultSet.next()) { //checks if there's an account with the matching id
                idList.add(resultSet.getString("ID")); //Just getting the ID's
            }

        } catch (SQLException e) {
            logger.error("Database Error Occurred", e);
            // If something does go wrong this will log the stack trace
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
            // And return a HTTP 500 error to the requester
        }
        for(int i = 0; i < idList.size(); i++ ) {
            if (idList.get(i).equals(userId)) { //Checks if its a valid id
                idfound = true;
                ctx.sendRedirect("/" + userId); //redirects to the account page
                break;
            }
        }
            if(idfound==false){
                throw new StatusCodeException(StatusCode.NOT_FOUND, "Invalid User ID"); //error 404
            }
        }



    @GET("/transactions/{id}")
    public String transactionDetails(Context ctx) {
        String transID = ctx.path("id").value();

        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM `Transactions` WHERE `ID` = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) { //using prepared statement to make sure the datatype it expects is correct
                statement.setString(1, transID);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) { //checks if there's a transaction with the matching id
                    String id = resultSet.getString("ID");
                    String type = resultSet.getString("Type");
                    double amount = resultSet.getDouble("Amount");
                    String to = resultSet.getString("To");
                    String from = resultSet.getString("From");

                    return "ID: " + id + ", Type: " + type + ", Amount: " + amount + ", To: " + to + ", From: " + from; //returns matching trans
                } else {
                    return "User not found"; //returns error statement when no matching transaction is detected
                }
            }
        } catch (SQLException e) {
            logger.error("Database Error Occurred", e);
            // If something does go wrong this will log the stack trace
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
            // And return a HTTP 500 error to the requester
        }
    }

}
