/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import static com.opengamma.sdk.common.ServiceInvoker.SERVICE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import com.opengamma.sdk.common.auth.AuthClient;
import com.opengamma.sdk.common.auth.AuthenticationException;
import com.opengamma.sdk.common.auth.Credentials;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Test.
 */
public class BasicTest {

  private static final Credentials CREDENTIALS = Credentials.ofApiKey("user", "pw");
  private static final Credentials BAD_CREDENTIALS = Credentials.ofApiKey("bad", "pw");

  @Test
  public void testBasics() {
    assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> ServiceInvoker.of(null));
  }

  @Test
  public void testAuthGood() {
    AuthClient mockAuth = new TestingAuthClient();
    @SuppressWarnings("resource")
    ServiceInvoker invoker = ServiceInvoker.builder(CREDENTIALS)
        .authClientFactory(inv -> mockAuth)
        .build();
    assertThat(invoker.getServiceUrl()).isEqualTo(SERVICE_URL);
    assertThat(invoker.getHttpClient().interceptors()).hasSize(4);
    assertThat(invoker.getHttpClient().followRedirects()).isTrue();
    assertThat(invoker.getExecutor().isShutdown()).isFalse();
    invoker.close();
    assertThat(invoker.getExecutor().isShutdown()).isTrue();
  }

  @Test
  public void testAuthGoodHttpFactory() {
    AuthClient mockAuth = new TestingAuthClient();
    try (ServiceInvoker invoker = ServiceInvoker.builder(CREDENTIALS)
        .httpClientFactory(
            builder -> builder.addInterceptor(chain -> chain.proceed(chain.request())).followRedirects(false).build())
        .authClientFactory(inv -> mockAuth)
        .build()) {
      assertThat(invoker.getServiceUrl()).isEqualTo(SERVICE_URL);
      assertThat(invoker.getHttpClient().interceptors()).hasSize(5);  // logging, user-agent & auth& retry plus one from test
      assertThat(invoker.getHttpClient().followRedirects()).isFalse();
    }
  }

  @Test
  public void testAuthGoodHttpClient() {
    AuthClient mockAuth = new TestingAuthClient();
    try (ServiceInvoker invoker = ServiceInvoker.builder(CREDENTIALS)
        .httpClient(new OkHttpClient())
        .authClientFactory(inv -> mockAuth)
        .build()) {
      assertThat(invoker.getServiceUrl()).isEqualTo(SERVICE_URL);
      assertThat(invoker.getHttpClient().interceptors()).hasSize(3);  // user-agent & auth & retry
    }
  }

  @Test
  public void testAuthBad() {
    AuthClient mockAuth = new TestingAuthClient();
    try (ServiceInvoker serviceInvoker = ServiceInvoker.builder(BAD_CREDENTIALS).authClientFactory(inv -> mockAuth).build()) {
      Request testRequest = new Request.Builder()
          .url(serviceInvoker.getServiceUrl().resolve("/test"))
          .get()
          .build();
      assertThatExceptionOfType(AuthenticationException.class).isThrownBy(() -> serviceInvoker.getHttpClient().newCall(testRequest).execute());
    }
  }

}
