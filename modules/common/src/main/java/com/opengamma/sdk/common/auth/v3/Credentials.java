/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth.v3;

/**
 * Credentials used to authenticate with the service.
 */
public interface Credentials {

  /**
   * Obtains credentials using an API key and secret.
   * <p>
   * This is the recommended way to connect.
   * 
   * @param apiKey  the API key
   * @param secret  the secret
   * @return the credentials
   */
  public static Credentials ofApiKey(String apiKey, String secret) {
    return ApiKeyCredentials.of(apiKey, secret);
  }

  //-------------------------------------------------------------------------
  /**
   * Authenticates, returning an access token.
   * 
   * @param client  the client to authenticate with
   * @return the access token result
   * @throws RuntimeException if the token cannot be obtained
   */
  public abstract AccessTokenResult authenticate(AuthClient client);

}
