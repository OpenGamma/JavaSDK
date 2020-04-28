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
    // first call - auth
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
        "  \"access_token\": \"testAccessToken\",\n" +
        "  \"expires_in\": 1,\n" +
        "  \"token_type\": \"Bearer\"\n" +
        "}"));
    // first call - valid result
    server.enqueue(new MockResponse().setResponseCode(200).setBody("VALID1"));
    // second call - token expired
    server.enqueue(new MockResponse().setResponseCode(401).setBody(""));
    // second call - auth
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
        "  \"access_token\": \"testAccessToken\",\n" +
        "  \"expires_in\": 1,\n" +
        "  \"token_type\": \"Bearer\"\n" +
        "}"));
    // second call - valid result
    server.enqueue(new MockResponse().setResponseCode(200).setBody("VALID2"));
  }

  @AfterEach
  public void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  public void testAuthFlow() throws Exception {
    try (ServiceInvoker invoker = ServiceInvoker.builder(Credentials.ofApiKey("test", "test"))
        .serviceUrl(server.url(""))
        .build()) {

      Request request = new Request.Builder()
          .url(invoker.getServiceUrl().resolve("test"))
          .get()
          .build();
      // first call
      try (Response response1 = invoker.getHttpClient().newCall(request).execute()) {
        assertThat(response1.code()).isEqualTo(200);
        assertThat(response1.message()).isEqualTo("OK");
        assertThat(response1.body().string()).isEqualTo("VALID1");
        assertThat(response1.isSuccessful()).isTrue();
      }
      // second call
      try (Response response2 = invoker.getHttpClient().newCall(request).execute()) {
        assertThat(response2.code()).isEqualTo(200);
        assertThat(response2.message()).isEqualTo("OK");
        assertThat(response2.body().string()).isEqualTo("VALID2");
        assertThat(response2.isSuccessful()).isTrue();
      }
    }
  }

}
