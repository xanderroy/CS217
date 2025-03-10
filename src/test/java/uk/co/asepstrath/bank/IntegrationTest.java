package uk.co.asepstrath.bank;

import io.jooby.StatusCode;
import io.jooby.test.JoobyTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JoobyTest(App.class)
public class IntegrationTest {
    OkHttpClient client = new OkHttpClient();

    @Test
    public void testAllAccounts(int serverPort) throws IOException {
        Request req = new Request.Builder().url("http://localhost:" + serverPort + "/bank/accounts").build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }

    @Test
    public void testOneAccount(int serverPort) throws IOException {
        Request req = new Request.Builder().url("http://localhost:" + serverPort + "/bank/c9dfe369-c5f8-44fd-b9e2-f4fc5ac56ac2").build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }

    @Test
    public void testTransWorks(int serverPort) throws IOException {
        Request req = new Request.Builder().url("http://localhost:" + serverPort + "/bank/transactions/b32c1c03-6352-482c-a03d-b381a24fc142").build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }
}
