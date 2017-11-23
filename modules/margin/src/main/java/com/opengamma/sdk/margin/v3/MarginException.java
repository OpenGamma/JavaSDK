/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.v3;

/**
 * Used to indicate that there was a problem with a margin request.
 */
public final class MarginException extends IllegalStateException {

  /** Serialization version. */
  private static final long serialVersionUID = 8789233267870746967L;

  /**
   * Creates an instance from a message.
   * 
   * @param message the message
   */
  public MarginException(String message) {
    super(message);
  }

}
