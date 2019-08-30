/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opengamma.sdk.common.auth.Credentials;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Test {@link ServiceInvoker}.
 */
public class ServiceInvokerTest {

  private MockWebServer server;

  @BeforeEach
  public void setUp() throws Exception {
    server = new MockWebServer();
    server.start(18080);
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
        "  \"access_token\": \"testAccessToken\",\n" +
        "  \"expires_in\": 1,\n" +
        "  \"token_type\": \"Bearer\"\n" +
        "}"));
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
        "  \"access_token\": \"<Your access token here>\",\n" +
        "  \"expires_in\": 1,\n" +
        "  \"token_type\": \"Bearer\"\n" +
        "}"));
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
  }

  @AfterEach
  public void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  public void testNewRequestWithReauthenticationSequence() throws Exception {

    try (ServiceInvoker invoker = ServiceInvoker.builder(Credentials.ofApiKey("test", "test"))
        .serviceUrl(HttpUrl.parse("http://" + server.getHostName() + ":" + server.getPort()))
        .build()) {

      Request request = new Request.Builder()
          .url(invoker.getServiceUrl().resolve("test"))
          .get()
          .build();
      Response response = invoker.getHttpClient().newCall(request).execute();
      assertThat(response.code()).isEqualTo(200);
      assertThat(response.message()).isEqualTo("OK");
      assertThat(response.isSuccessful()).isTrue();
    }
  }
}
