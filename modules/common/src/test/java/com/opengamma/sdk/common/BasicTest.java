/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import static com.opengamma.sdk.common.ServiceInvoker.SERVICE_URL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import org.testng.annotations.Test;

import com.opengamma.sdk.common.auth.AuthClient;
import com.opengamma.sdk.common.auth.AuthenticationException;
import com.opengamma.sdk.common.auth.Credentials;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Test.
 */
@Test
public class BasicTest {

  private static final Credentials CREDENTIALS = Credentials.ofApiKey("user", "pw");
  private static final Credentials BAD_CREDENTIALS = Credentials.ofApiKey("bad", "pw");

  public void testBasics() {
    assertThrows(NullPointerException.class, () -> ServiceInvoker.of(null));
  }

  public void testAuthGood() {
    AuthClient mockAuth = new TestingAuthClient();
    @SuppressWarnings("resource")
    ServiceInvoker invoker = ServiceInvoker.builder(CREDENTIALS)
        .authClientFactory(inv -> mockAuth)
        .build();
    assertEquals(invoker.getServiceUrl(), SERVICE_URL);
    assertEquals(invoker.getHttpClient().interceptors().size(), 3);
    assertEquals(invoker.getHttpClient().followRedirects(), true);
    assertEquals(invoker.getExecutor().isShutdown(), false);
    invoker.close();
    assertEquals(invoker.getExecutor().isShutdown(), true);
  }

  public void testAuthGoodHttpFactory() {
    AuthClient mockAuth = new TestingAuthClient();
    try (ServiceInvoker invoker = ServiceInvoker.builder(CREDENTIALS)
        .httpClientFactory(
            builder -> builder.addInterceptor(chain -> chain.proceed(chain.request())).followRedirects(false).build())
        .authClientFactory(inv -> mockAuth)
        .build()) {
      assertEquals(invoker.getServiceUrl(), SERVICE_URL);
      assertEquals(invoker.getHttpClient().interceptors().size(), 4);  // logging, user-agent & auth plus one from test
      assertEquals(invoker.getHttpClient().followRedirects(), false);
    }
  }

  public void testAuthGoodHttpClient() {
    AuthClient mockAuth = new TestingAuthClient();
    try (ServiceInvoker invoker = ServiceInvoker.builder(CREDENTIALS)
        .httpClient(new OkHttpClient())
        .authClientFactory(inv -> mockAuth)
        .build()) {
      assertEquals(invoker.getServiceUrl(), SERVICE_URL);
      assertEquals(invoker.getHttpClient().interceptors().size(), 2);  // user-agent & auth
    }
  }

  public void testAuthBad() {
    AuthClient mockAuth = new TestingAuthClient();
    try (ServiceInvoker serviceInvoker = ServiceInvoker.builder(BAD_CREDENTIALS).authClientFactory(inv -> mockAuth).build()) {
      Request testRequest = new Request.Builder()
          .url(serviceInvoker.getServiceUrl().resolve("/test"))
          .get()
          .build();
      assertThrows(AuthenticationException.class, () -> serviceInvoker.getHttpClient().newCall(testRequest).execute());
    }
  }

}
