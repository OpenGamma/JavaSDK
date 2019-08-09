/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import static com.opengamma.sdk.common.ServiceInvoker.SERVICE_URL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

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
    assertEquals(invoker.getHttpClient().interceptors().size(), 4);
    assertTrue(invoker.getHttpClient().followRedirects());
    assertFalse(invoker.getExecutor().isShutdown());
    invoker.close();
    assertTrue(invoker.getExecutor().isShutdown());
  }

  public void testAuthGoodHttpFactory() {
    AuthClient mockAuth = new TestingAuthClient();
    try (ServiceInvoker invoker = ServiceInvoker.builder(CREDENTIALS)
        .httpClientFactory(
            builder -> builder.addInterceptor(chain -> chain.proceed(chain.request())).followRedirects(false).build())
        .authClientFactory(inv -> mockAuth)
        .build()) {
      assertEquals(invoker.getServiceUrl(), SERVICE_URL);
      assertEquals(invoker.getHttpClient().interceptors().size(), 5);  // logging, user-agent & auth& retry plus one from test
      assertFalse(invoker.getHttpClient().followRedirects());
    }
  }

  public void testAuthGoodHttpClient() {
    AuthClient mockAuth = new TestingAuthClient();
    try (ServiceInvoker invoker = ServiceInvoker.builder(CREDENTIALS)
        .httpClient(new OkHttpClient())
        .authClientFactory(inv -> mockAuth)
        .build()) {
      assertEquals(invoker.getServiceUrl(), SERVICE_URL);
      assertEquals(invoker.getHttpClient().interceptors().size(), 3);  // user-agent & auth & retry
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
