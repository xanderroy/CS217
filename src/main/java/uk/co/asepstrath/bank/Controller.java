package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;

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
public class  Controller {
    private final DataSource dataSource;
    private final Logger logger;

    public Controller(DataSource ds, Logger log) {
        dataSource = ds;
        logger = log;
    }

    @GET("/welcome")
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
}
