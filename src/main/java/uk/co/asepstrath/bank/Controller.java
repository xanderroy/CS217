package uk.co.asepstrath.bank;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Path("/bank")
public class Controller {
    private final DataSource dataSource;
    private final Logger logger;
    private boolean isAdmin = false;
    private boolean isLoggedIn = false;
    private String currentUserId = "";

    public Controller(DataSource ds, Logger log) {
        dataSource = ds;
        logger = log;
    }
    @GET("/")
    public ModelAndView homepage() {
        return new ModelAndView("homepage.hbs", null);
    }

    @GET("/accounts")
    public ModelAndView showAccounts() {
        if(isAdmin){
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
        } else{
            return new ModelAndView("login.hbs", null);
        }
    }

    @GET("/{id}")
    public ModelAndView UserDetails(Context ctx) {
        String userId = ctx.path("id").value(); //sets the id from url as a string
        Map<String, Object> model = new HashMap<>();
        if(!(currentUserId.equals(userId) || isAdmin)){
            model.put("error", "Invalid UserID.");
            return new ModelAndView("userDetails.hbs", model);
        }else{
            try (Connection connection = dataSource.getConnection()) {
                String query = "SELECT * FROM Accounts WHERE ID = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) { //using prepared statement to make sure the datatype it expects is correct
                    statement.setString(1, userId); //puts the id into the query
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) { //checks if there's an account with the matching id
                        model.put("id", resultSet.getString("ID"));
                        model.put("name", resultSet.getString("Name"));
                        double balance = Accounts.getAccount(userId).getBalance();
                        model.put("balance", balance);
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
            return new ModelAndView("user.hbs", model);
        }
    }


    @GET("/{id}/transactions")
    public ModelAndView AllTransAcc(Context ctx) {
        String accountid = ctx.path("id").value();
        ArrayList<Map<String, Object>> transactions = getAccTransactions(accountid);

        Map<String, Object> model = new HashMap<>();
        model.put("transactions", transactions);
        return new ModelAndView("transactions.hbs", model);
    }

    public ArrayList<Map<String, Object>> getAccTransactions(String id) {
        ArrayList<Map<String, Object>> transactions = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = ("SELECT * FROM `Transactions` WHERE `To` = ? OR `From` = ?"); //transaction is relevant to account
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", rs.getString("ID"));
                transaction.put("amount", rs.getDouble("Amount"));
                transaction.put("type", rs.getString("Type"));
                transaction.put("to", rs.getString("To"));
                transaction.put("from", rs.getString("From"));
                transactions.add(transaction);
            }
        } catch (Exception e) {
            logger.error("Error in database", e);
        }

        return transactions;
    }


    @GET("/login")
    public ModelAndView loginPage(Context ctx) {
         return new ModelAndView("login.hbs", null);
    }
    @GET("/login")
    public ModelAndView loginSite(@QueryParam("userId") String userId, Context ctx) {
        if (userId == null || userId.trim().isEmpty()) {
            return new ModelAndView("login.hbs", null);
        } else if(userId.equals("admin")){
            ctx.sendRedirect("/bank/accounts");
            isAdmin = true;
            isLoggedIn = true;
            currentUserId = userId;
        }
        userId = userId.trim();
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
        for (int i = 0; i < idList.size(); i++) {
            if (idList.get(i).equals(userId)) { //Checks if its a valid id
                idfound = true;
                isLoggedIn = true;
                isAdmin = false;
                currentUserId = userId;
                ctx.sendRedirect("/bank/" + userId); //redirects to the account page
                break;
            }
        }
        if (idfound == false) {
            Map<String, String> model = new HashMap<>();
            model.put("error", "Invalid UserID. Please try again.");
            return new ModelAndView("login.hbs", model);
        }
        return new ModelAndView("login.hbs", null);
    }

    @GET("/logout")
    public ModelAndView logout() {
        Map<String, String> model = new HashMap<>();
        if (isLoggedIn) {
            isAdmin = false;
            isLoggedIn = false;
            currentUserId = "";
            return new ModelAndView("logout.hbs", null);
        }
        model.put("error", "Not logged in");
        return new ModelAndView("logout.hbs", model);
    }


    @GET("/transactions/{id}")
    public ModelAndView transactionDetails(Context ctx) {
        String transID = ctx.path("id").value();
        Map<String, Object> model = new HashMap<>();
            try (Connection connection = dataSource.getConnection()) {
                String query = "SELECT * FROM `Transactions` WHERE `ID` = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, transID);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        model.put("id", resultSet.getString("ID"));
                        model.put("type", resultSet.getString("Type"));
                        model.put("amount", resultSet.getDouble("Amount"));
                        model.put("to", resultSet.getString("To"));
                        model.put("from", resultSet.getString("From"));
                    } else {
                        model.put("error", "Transaction not found");
                    }
                }
            } catch (SQLException e) {
                logger.error("Database Error Occurred", e);
                throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
            }
            if(!(currentUserId.equals(model.get("to")) || currentUserId.equals(model.get("from")))){
                model.put("error", "Invalid TransactionID.");
                return new ModelAndView("transactionDetails.hbs", model);
            } else {
                return new ModelAndView("transactionDetails.hbs", model);
            }
        }

    @GET("/{id}/summary")
    public ModelAndView summaryOfSpending(Context ctx) {
        String id = ctx.path("id").value();
        HashMap<String, Double> summary = new HashMap<>();
        ArrayList<Map<String, Object>> transactions = getAccTransactions(id);

        for (int i = 0; i < transactions.size(); i++) {
            if (Objects.equals(transactions.get(i).get("type").toString(), "PAYMENT")) {
                String businessID = transactions.get(i).get("to").toString();

                // Assuming Businesses is a class that provides business categories by business ID.
                String category = Businesses.getBusinessByID(businessID).getCategory();

                if (summary.get(category) == null) {
                    summary.put(category, (Double) transactions.get(i).get("amount"));
                } else {
                    summary.replace(category, summary.get(category) + (Double) transactions.get(i).get("amount"));
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("summary", summary);  // Passing the summary data to the view.
        return new ModelAndView("summary.hbs", model);  // Rendering the summary view.
    }

    @GET("/report")
    public ArrayList<ArrayList<String>> sanctionReport() {
        ArrayList<ArrayList<String>> list = Businesses.sanctionedBusinesses();

        return list;
    }

}

