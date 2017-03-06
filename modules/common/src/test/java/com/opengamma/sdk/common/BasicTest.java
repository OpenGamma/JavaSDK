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
import com.opengamma.sdk.common.auth.Credentials;

/**
 * Test.
 */
@Test
public class BasicTest {

  private static final Credentials CREDENTIALS = Credentials.ofUsernamePassword("user", "pw");
  private static final Credentials BAD_CREDENTIALS = Credentials.ofUsernamePassword("bad", "pw");

  public void testBasics() {
    assertThrows(NullPointerException.class, () -> ServiceInvoker.of(CREDENTIALS, null));
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
    assertThrows(IllegalStateException.class, () -> ServiceInvoker.of(BAD_CREDENTIALS, SERVICE_URL, mockAuth));
  }

}
