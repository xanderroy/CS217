package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.ModelAndView;
import io.jooby.test.JoobyTest;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import io.jooby.netty.NettyServer;

import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;

import javax.sql.DataSource;

public class ControllerTests extends Jooby {
    {
        install(new HikariModule("mem"));
    }

    public Controller setup() {
        API api = new API(require(DataSource.class), getLog());
        api.getAccounts();
        return new Controller(require(DataSource.class), getLog(), true, true, "admin");
    }

    @org.junit.Test
    @Test
    public void testLogin() {
        Controller controller = setup();
        ModelAndView model = controller.showAccounts();
        Assertions.assertNotNull(model.getView());
        Assertions.assertNotNull(model.getModel());
    }
}
