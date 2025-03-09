package uk.co.asepstrath.bank;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import io.jooby.*;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
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
    public ModelAndView showAccounts() {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Accounts");

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
}