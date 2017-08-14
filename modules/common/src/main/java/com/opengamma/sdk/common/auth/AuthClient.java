/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth;

import com.opengamma.sdk.common.ServiceInvoker;

/**
 * Client providing access to the auth service.
 * <p>
 * Applications should not need to use this service directly.
 * The {@link ServiceInvoker} invokes authentication when required.
 *
 * @deprecated A new version of this interface exists in the v3 package. This interface will be removed in future versions
 */
@Deprecated
public interface AuthClient {

  /**
   * Obtains an instance.
   * 
   * @param invoker  the service invoker
   * @return the client
   */
  public static AuthClient of(ServiceInvoker invoker) {
    return InvokerAuthClient.of(invoker);
  }

  //-------------------------------------------------------------------------
  /**
   * Authenticates the user based on a username and password.
   * 
   * @param username  the username
   * @param password  the password
   * @return the result containing the access token
   */
  public abstract AccessTokenResult authenticatePassword(String username, String password);

  /**
   * Authenticates the user based on an API key.
   * 
   * @param apiKey  the API key
   * @param apiKeySecret  the secret
   * @return the result containing the access token
   */
  public abstract AccessTokenResult authenticateApiKey(String apiKey, String apiKeySecret);

  /**
   * Refreshes the main token based on the refresh token.
   * 
   * @param refreshToken  the refresh token
   * @return the result containing the access token
   */
  public abstract AccessTokenResult refreshToken(String refreshToken);

}
