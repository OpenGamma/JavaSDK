/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import com.opengamma.sdk.common.JavaSdkException;

/**
 * Used to indicate that there was a problem with a margin request.
 */
public class MarginException extends JavaSdkException {

  /** The Margin operation which is associated with the current exception. This is optional, as some exceptions are not mapped to a single operation. */
  private final MarginOperation marginOperation;

  /** Serialization version. */
  private static final long serialVersionUID = 8789233267870746967L;

  /**
   * Creates an instance from a message and reason.
   * 
   * @param reason  the reason sent by the remote API call
   * @param message  the message
   */
  public MarginException(String message, String reason) {
    super(message, reason);
    marginOperation = null;
  }

  /**
   * Creates an instance from a message, HTTP code, reason and description.
   * 
   * @param httpCode  the HTTP code of the remote API call
   * @param reason  the reason sent by the remote API call
   * @param description  the description sent by the remote API call
   * @param message  the message
   */
  public MarginException(String message, int httpCode, String reason, String description) {
    super(message, httpCode, reason, description);
    marginOperation = null;
  }

  /**
   * Creates an instance from a message, HTTP code, reason and description.
   *
   * @param httpCode  the HTTP code of the remote API call
   * @param reason  the reason sent by the remote API call
   * @param description  the description sent by the remote API call
   * @param message  the message
   * @param operation  the Margin Service API operation which was being performed while the Exception occurred
   */
  public MarginException(String message, int httpCode, String reason, String description, MarginOperation operation) {
    super(message, httpCode, reason, description);
    this.marginOperation = operation;
  }

  /**
   * Gets the margin operation responsible for the current exception.
   *
   * @return the Margin Operation. This can be null, if there is no margin operation associated with the current exception
   */
  public MarginOperation getMarginOperation() {
    return marginOperation;
  }
}
