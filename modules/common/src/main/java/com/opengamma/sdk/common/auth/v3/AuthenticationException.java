/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth.v3;

import com.opengamma.sdk.common.v3.JavaSdkException;

/**
 * Used to indicate that there was a problem with authentication.
 */
public class AuthenticationException extends JavaSdkException {

  /** Serialization version. */
  private static final long serialVersionUID = -4839280757656844970L;

  /**
   * Creates an instance from a message and reason.
   * 
   * @param reason  the reason sent by the remote API call
   * @param message  the message
   */
  public AuthenticationException(String message, String reason) {
    super(message, reason);
  }

  /**
   * Creates an instance from a message, HTTP code, reason and description.
   * 
   * @param httpCode  the HTTP code of the remote API call
   * @param reason  the reason sent by the remote API call
   * @param description  the description sent by the remote API call
   * @param message  the message
   */
  public AuthenticationException(String message, int httpCode, String reason, String description) {
    super(message, httpCode, reason, description);
  }

}
