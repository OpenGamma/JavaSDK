/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import static com.opengamma.sdk.common.v3.ServiceInvoker.SERVICE_URL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import org.testng.annotations.Test;

import com.opengamma.sdk.common.auth.v3.AuthClient;
import com.opengamma.sdk.common.auth.v3.AuthenticationException;
import com.opengamma.sdk.common.auth.v3.Credentials;
import com.opengamma.sdk.common.v3.ServiceInvoker;

import okhttp3.HttpUrl;

/**
 * Test.
 */
@Test
public class BasicTest {

  private static final Credentials CREDENTIALS = Credentials.ofApiKey("user", "pw");
  private static final Credentials BAD_CREDENTIALS = Credentials.ofApiKey("bad", "pw");

  public void testBasics() {
    assertThrows(NullPointerException.class, () -> ServiceInvoker.of(CREDENTIALS, (HttpUrl) null));
    assertThrows(NullPointerException.class, () -> ServiceInvoker.of(null, SERVICE_URL));
  }

  public void testAuthGood() {
    AuthClient mockAuth = new TestingAuthClient();
    @SuppressWarnings("resource")
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, SERVICE_URL, mockAuth);
    assertEquals(invoker.getServiceUrl(), SERVICE_URL);
    assertEquals(invoker.getHttpClient().interceptors().size(), 3);
    assertEquals(invoker.getExecutor().isShutdown(), false);
    invoker.close();
    assertEquals(invoker.getExecutor().isShutdown(), true);
  }

  public void testAuthBad() {
    AuthClient mockAuth = new TestingAuthClient();
    assertThrows(AuthenticationException.class, () -> ServiceInvoker.of(BAD_CREDENTIALS, SERVICE_URL, mockAuth));
  }

}
