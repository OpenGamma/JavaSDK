/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import com.opengamma.sdk.common.auth.AccessTokenResult;
import com.opengamma.sdk.common.auth.AuthClient;

/**
 * Mock auth client for testing.
 */
@SuppressWarnings("deprecation")
public final class TestingAuthClient implements AuthClient {

  @Override
  public AccessTokenResult authenticatePassword(String username, String password) {
    if (username.equals("bad")) {
      throw new IllegalStateException("User rejected: bad");
    }
    return AccessTokenResult.of("1234", "bearer", 60_000, "4321");
  }

  @Override
  public AccessTokenResult authenticateApiKey(String apiKey, String apiKeySecret) {
    if (apiKey.equals("bad")) {
      throw new IllegalStateException("API key rejected: bad");
    }
    return AccessTokenResult.of("1234", "bearer", 60_000, "4321");
  }

  @Override
  public AccessTokenResult refreshToken(String refreshToken) {
    if (refreshToken.equals("bad")) {
      throw new IllegalStateException("Refresh token rejected: bad");
    }
    return AccessTokenResult.of("1234", "bearer", 60_000, "4321");
  }

}
