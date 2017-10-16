/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.v3;

import com.opengamma.sdk.common.auth.v3.AccessTokenResult;
import com.opengamma.sdk.common.auth.v3.ApiKeyCredentials;
import com.opengamma.sdk.common.auth.v3.AuthClient;

/**
 * Mock auth client for testing.
 */
public final class TestingAuthClient implements AuthClient {

  @Override
  public AccessTokenResult authenticateApiKey(String apiKey, String apiKeySecret) {
    if (apiKey.equals("bad")) {
      throw new IllegalStateException("API key rejected: bad");
    }
    return AccessTokenResult.of("1234", "bearer", 60_000, ApiKeyCredentials.of("1234", "secret"));
  }

  @Override
  public AccessTokenResult authenticateApiKey(ApiKeyCredentials credentials) {
    return credentials.authenticate(this);
  }


}
