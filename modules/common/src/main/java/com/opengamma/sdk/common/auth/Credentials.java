/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth;

import java.io.UncheckedIOException;

/**
 * Credentials used to authenticate with the service.
 */
public interface Credentials {
  // the design of this interface and the methods that use it are intended to support
  // different ways to authenticate without exposing the implementing classes

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
   * Authenticates using these credentials, returning an access token.
   * 
   * @param client  the client to authenticate with
   * @return the access token result
   * @throws AuthenticationException if unable to authenticate
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract AccessTokenResult authenticate(AuthClient client);

}
