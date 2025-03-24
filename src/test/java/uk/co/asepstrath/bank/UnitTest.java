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
            ResultSet rs = stmt.executeQuery("SELECT * FROM `Accounts` WHERE `ID` = 'f38e72e5-16e0-4f53-bce2-5d78e46649c7'");
            rs.next();
            assertEquals("f38e72e5-16e0-4f53-bce2-5d78e46649c7", rs.getString("ID"));
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
            ResultSet rs = stmt.executeQuery("SELECT * FROM `Transactions` WHERE `ID` = '2b08bc6f-be02-406a-8d3d-f06d529945db'");
            rs.next();
            assertEquals("2b08bc6f-be02-406a-8d3d-f06d529945db", rs.getString("ID"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}

