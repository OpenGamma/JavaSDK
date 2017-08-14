/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth.v3;


import com.opengamma.sdk.common.v3.ServiceInvoker;

/**
 * Client providing access to the auth service.
 * <p>
 * Applications should not need to use this service directly.
 * The {@link ServiceInvoker} invokes authentication when required.
 */
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

  /**
   * Authenticates the user based on an API key.
   * 
   * @param apiKey  the API key
   * @param apiKeySecret  the secret
   * @return the result containing the access token
   */
  public abstract AccessTokenResult authenticateApiKey(String apiKey, String apiKeySecret);

  public abstract AccessTokenResult authenticateApiKey(ApiKeyCredentials credentials);

}
