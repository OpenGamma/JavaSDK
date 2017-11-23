/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth.v3;

/**
 * Used to indicate that there was a problem with authentication.
 */
public final class AuthenticationException extends IllegalStateException {

  /** Serialization version. */
  private static final long serialVersionUID = -4839280757656844970L;

  /**
   * Creates an instance from a message.
   * 
   * @param message the message
   */
  public AuthenticationException(String message) {
    super(message);
  }

}
