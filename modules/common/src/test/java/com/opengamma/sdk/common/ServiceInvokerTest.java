package com.opengamma.sdk.common;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.opengamma.sdk.common.auth.Credentials;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@Test
public class ServiceInvokerTest {

  private MockWebServer server;

  @BeforeMethod
  public void setUp() throws Exception {
    server = new MockWebServer();
    server.start(18080);
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
        "  \"access_token\": \"<Your access token here>\",\n" +
        "  \"expires_in\": 1,\n" +
        "  \"token_type\": \"Bearer\"\n" +
        "}"));
    server.enqueue(new MockResponse().setResponseCode(401).setBody("{\n" +
        "\"message\": \"Unauthorized\"\n" +
        "}"));
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
        "  \"access_token\": \"<Your access token here>\",\n" +
        "  \"expires_in\": 1,\n" +
        "  \"token_type\": \"Bearer\"\n" +
        "}"));
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
  }

  @AfterMethod
  public void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  public void testName() throws Exception {

    try (ServiceInvoker invoker = ServiceInvoker.builder(Credentials.ofApiKey("test", "test"))
        .serviceUrl(HttpUrl.parse("http://" + server.getHostName() + ":" + server.getPort()))
        .build()) {

      Request request = new Request.Builder()
          .url(invoker.getServiceUrl().resolve("test"))
          .get()
          .build();
      Response response = invoker.getHttpClient().newCall(request).execute();
    }
  }
}