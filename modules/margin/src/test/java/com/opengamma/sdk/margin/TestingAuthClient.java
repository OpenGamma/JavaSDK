/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import com.opengamma.sdk.common.auth.AccessTokenResult;
import com.opengamma.sdk.common.auth.AuthClient;
import com.opengamma.sdk.common.auth.AuthenticationException;
import com.opengamma.sdk.common.auth.Credentials;

/**
 * Mock auth client for testing.
 */
public final class TestingAuthClient implements AuthClient {

  @Override
  public AccessTokenResult authenticateApiKey(String apiKey, String apiKeySecret) {
    if (apiKey.equals("bad")) {
      throw new AuthenticationException("API key rejected: bad", "Bad");
    }
    return AccessTokenResult.of("1234", "bearer", 60_000, Credentials.ofApiKey("1234", "secret"));
  }

  @Override
  public AccessTokenResult authenticateApiKey(Credentials credentials) {
    return credentials.authenticate(this);
  }

}
