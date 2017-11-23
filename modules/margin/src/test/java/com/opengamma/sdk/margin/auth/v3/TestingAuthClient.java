/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.auth.v3;

import com.opengamma.sdk.common.auth.v3.AccessTokenResult;
import com.opengamma.sdk.common.auth.v3.AuthClient;
import com.opengamma.sdk.common.auth.v3.AuthenticationException;
import com.opengamma.sdk.common.auth.v3.Credentials;

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
