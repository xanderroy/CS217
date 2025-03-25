package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.ModelAndView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import io.jooby.hikari.HikariModule;


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
}
