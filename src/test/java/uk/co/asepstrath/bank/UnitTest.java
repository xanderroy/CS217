package uk.co.asepstrath.bank;

import static org.junit.jupiter.api.Assertions.*;

import io.jooby.*;
import io.jooby.hikari.HikariModule;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import org.slf4j.Logger;



public class UnitTest extends Jooby {
    { install(new HikariModule("mem")); }
    DataSource ds = require(DataSource.class);
    Logger log = getLog();

    API api = new API(ds, log);

    @Test
    public void testAccountsDB() {
        api.getAccounts();
        try( Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `Accounts` WHERE `ID` = '0002c352-6f10-4bd4-8783-074705090db4'");
            rs.next();
            assertEquals("0002c352-6f10-4bd4-8783-074705090db4", rs.getString("ID"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void testTransactionsDB() {
        api.getTransactions();
        try( Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `Transactions` WHERE `ID` = '0167a486-3606-4841-86e6-6bd44786972d'");
            rs.next();
            assertEquals("0167a486-3606-4841-86e6-6bd44786972d", rs.getString("ID"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}

