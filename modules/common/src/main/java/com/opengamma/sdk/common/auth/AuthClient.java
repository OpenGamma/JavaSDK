/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth;

import java.io.UncheckedIOException;

import com.opengamma.sdk.common.ServiceInvoker;

/**
 * Client providing access to the auth service.
 * <p>
 * Applications should not need to use this service directly.
 * The {@link ServiceInvoker} invokes authentication when required.
 */
public interface AuthClient {

  /**
   * Obtains an instance, specifying the invoker to use.
   *
   * @param invoker  the service invoker
   * @return the client
   */
  public static AuthClient of(ServiceInvoker invoker) {
    return InvokerAuthClient.of(invoker);
  }

  //-------------------------------------------------------------------------
  /**
   * Authenticates the user based on an API key.
   *
   * @param apiKey  the API key
   * @param apiKeySecret  the secret
   * @return the result containing the access token
   * @throws AuthenticationException if unable to authenticate
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract AccessTokenResult authenticateApiKey(String apiKey, String apiKeySecret);

  /**
   * Authenticates the user based on an API key.
   *
   * @param credentials  the API key and secret, encapsulated in {@code Credentials}
   * @return the result containing the access token
   * @throws AuthenticationException if unable to authenticate
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract AccessTokenResult authenticateApiKey(Credentials credentials);

}
