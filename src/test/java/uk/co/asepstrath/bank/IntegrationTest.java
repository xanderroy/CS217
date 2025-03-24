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
        Request req = new Request.Builder().url("http://localhost:" + serverPort + "/bank/f38e72e5-16e0-4f53-bce2-5d78e46649c7").build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }

    @Test
    public void testTransWorks(int serverPort) throws IOException {
        Request req = new Request.Builder().url("http://localhost:" + serverPort + "/bank/transactions/2b08bc6f-be02-406a-8d3d-f06d529945db").build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }

    @Test
    public void testOneAccountTrans(int serverPort) throws IOException {
        Request req = new Request.Builder().url("http://localhost:" + serverPort + "/bank/c9dfe369-c5f8-44fd-b9e2-f4fc5ac56ac2/transactions").build();

        try (Response rsp = client.newCall(req).execute()) {
            assertEquals(StatusCode.OK.value(), rsp.code());
        }
    }
}
