package uk.co.asepstrath.bank;

import io.jooby.netty.NettyServer;
import uk.co.asepstrath.bank.example.ExampleController;
import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.example.ExampleController_;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class App extends Jooby {

    {
        /*
        This section is used for setting up the Jooby Framework modules
         */
        install(new NettyServer());
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));

        /*
        This will host any files in src/main/resources/assets on <host>/assets
        For example in the dice template (dice.hbs) it references "assets/dice.png" which is in resources/assets folder
         */
        assets("/assets/*", "/assets");
        assets("/service_worker.js","/service_worker.js");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger log = getLog();

        mvc(new ExampleController_(ds,log));

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(() -> onStart());
        onStop(() -> onStop());
    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */
    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account("Rachel", 50));
        accounts.add(new Account("Monica", 100));
        accounts.add(new Account("Phoebe", 76));
        accounts.add(new Account("Joey", 23.90));
        accounts.add(new Account("Chandler", 3));
        accounts.add(new Account("Ross", 54.32));

        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE `Accounts` (`Name` varchar(255), `Balance` number)");
            for (Account a : accounts) {
                stmt.executeUpdate("INSERT INTO Accounts VALUES ('" + a.getName() + "', '" + a.getBalance() + "')");
            }
        } catch (SQLException e) {
            log.error("Database Creation Error",e);
        }
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
