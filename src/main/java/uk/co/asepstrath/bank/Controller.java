package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import io.jooby.Context;
import java.sql.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Path("/bank")
public class Controller {
    private final DataSource dataSource;
    private final Logger logger;

    public Controller(DataSource ds, Logger log) {
        dataSource = ds;
        logger = log;
    }

    @GET("/accounts")
    public ArrayList<String> welcomeFromDB() {
        // Create a connection
        try (Connection connection = dataSource.getConnection()) {
            ArrayList<String> details = new ArrayList<>();
            // Create Statement (batch of SQL Commands)
            Statement statement = connection.createStatement();
            // Perform SQL Query
            ResultSet set = statement.executeQuery("SELECT * FROM `Accounts`");

            while (set.next()) {
                String id = set.getString("ID");
                String name = set.getString("Name");
                double balance = set.getDouble("Balance");
                boolean roundup = set.getBoolean("RoundUp");

                details.add(id + ", " + name + ", " + balance + ", " + roundup +"\n");
            }
            return details;
        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            logger.error("Database Error Occurred", e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }

    @GET("/{id}")
    public String UserDetails(Context ctx) {
        String userId = ctx.path("id").value(); //sets the id from url as a string
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM `Accounts` WHERE `ID` = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) { //using prepared statement to make sure the datatype is correct
                statement.setString(1, userId); //puts the id into the query
                ResultSet resultSet = statement.executeQuery();
    
                if (resultSet.next()) { //checks if there's an account with the matching id
                    String id = resultSet.getString("ID");
                    String name = resultSet.getString("Name");
                    double balance = resultSet.getDouble("Balance");
                    boolean roundup = resultSet.getBoolean("RoundUp");
    
                    return "ID: " + id + ", Name: " + name + ", Balance: " + balance + ", RoundUp: " + roundup; //returns matching account
                } else {
                    return "User not found"; //returns error statement when no matching account is detected
                }
            }
        } catch (SQLException e) {
            logger.error("Database Error Occurred", e);
              // If something does go wrong this will log the stack trace
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
            // And return a HTTP 500 error to the requester
        }
    }}