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
            ResultSet rs = stmt.executeQuery("SELECT * FROM `Accounts` WHERE `ID` = '04f6ab33-8208-4234-aabd-b6a8be8493da'");
            rs.next();
            assertEquals("04f6ab33-8208-4234-aabd-b6a8be8493da", rs.getString("ID"));
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
            ResultSet rs = stmt.executeQuery("SELECT * FROM `Transactions` WHERE `ID` = 'b32c1c03-6352-482c-a03d-b381a24fc142'");
            rs.next();
            assertEquals("b32c1c03-6352-482c-a03d-b381a24fc142", rs.getString("ID"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}

