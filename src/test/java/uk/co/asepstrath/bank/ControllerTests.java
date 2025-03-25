package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.ModelAndView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import io.jooby.hikari.HikariModule;

import java.util.ArrayList;
import java.util.Arrays;


public class ControllerTests extends Jooby {
    {
        install(new HikariModule("mem"));
    }

    API api = new API(require(DataSource.class), getLog());

    public Controller adminSetup() {
        return new Controller(require(DataSource.class), getLog(), true, true, "admin");
    }

    @Test
    public void testLogin() {
        Controller controller = adminSetup();
        api.getAccounts();
        ModelAndView model = controller.showAccounts();
        Assertions.assertNotNull(model.getView());
        Assertions.assertNotNull(model.getModel());
    }

    @Test
    public void testAccountTransactions() {
        Controller controller = adminSetup();
        api.getAccounts();
        api.getTransactions();
        var model = controller.getAccTransactions("f38e72e5-16e0-4f53-bce2-5d78e46649c7");
        Assertions.assertNotNull(model);
    }

    @Test
    public void testSanctioned() {
        api.getBusinesses();
        ArrayList<String> businesses = new ArrayList<>();
        for (Business b : Businesses.businesses) {
            if (Businesses.getBusinessByID(b.getId()).isSanctioned()) {
                businesses.add(Businesses.getBusinessByID(b.getId()).getId());
            }
        }
        Assertions.assertEquals(new ArrayList<>(Arrays.asList("CEX", "HAR", "YAN")), businesses);
    }

    @Test
    public void testBigSpenders() {
        Controller controller = adminSetup();
        api.getAccounts();
        api.getTransactions();
        API.applyTransactions();
        var a = controller.bigSpenders();
        Assertions.assertNotNull(a);
    }


    @Test
    public void testReport() {
        Controller controller = adminSetup();
        api.getTransactions();
        api.getBusinesses();
        API.applyTransactions();
        var a = controller.sanctionReport();
        Assertions.assertNotNull(a);
    }
}
