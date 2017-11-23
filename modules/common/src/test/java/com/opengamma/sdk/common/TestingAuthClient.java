/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import com.opengamma.sdk.common.auth.v3.AccessTokenResult;
import com.opengamma.sdk.common.auth.v3.AuthClient;
import com.opengamma.sdk.common.auth.v3.AuthenticationException;
import com.opengamma.sdk.common.auth.v3.Credentials;

/**
 * Mock auth client for testing.
 */
public final class TestingAuthClient implements AuthClient {

  @Override
  public AccessTokenResult authenticateApiKey(Credentials credentials) {
    return AccessTokenResult.of("1234", "bearer", 60_000, credentials);
  }

  @Override
  public AccessTokenResult authenticateApiKey(String apiKey, String apiKeySecret) {
    if (apiKey.equals("bad")) {
      throw new AuthenticationException("API key rejected: bad");
    }
    return AccessTokenResult.of("1234", "bearer", 60_000, Credentials.ofApiKey(apiKey, apiKeySecret));
  }

}
