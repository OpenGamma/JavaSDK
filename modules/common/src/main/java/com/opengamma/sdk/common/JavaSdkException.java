/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

/**
 * Used to indicate that there was a problem with a JavaSDK request.
 */
public class JavaSdkException extends IllegalStateException {

  /** Serialization version. */
  private static final long serialVersionUID = 8758757646754L;

  /** The HTTP code. */
  private final int httpCode;
  /** The reason provided by the remote API. */
  private final String reason;
  /** The description provided by the remote API. */
  private final String description;

  /**
   * Creates an instance from a message and reason.
   * 
   * @param reason  the reason sent by the remote API call
   * @param message  the message
   */
  public JavaSdkException(String message, String reason) {
    super(message);
    this.httpCode = 0;
    this.reason = reason;
    this.description = null;
  }

  /**
   * Creates an instance from a message, HTTP code, reason and description.
   * 
   * @param httpCode  the HTTP code of the remote API call
   * @param reason  the reason sent by the remote API call
   * @param description  the description sent by the remote API call
   * @param message  the message
   */
  public JavaSdkException(String message, int httpCode, String reason, String description) {
    super(message);
    this.httpCode = httpCode;
    this.reason = reason;
    this.description = description;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the reason code of the exception.
   * 
   * @return the reason
   */
  public String getReason() {
    return reason;
  }

  /**
   * Gets the HTTP code sent by the remote API call, zero if exception not from a remote call.
   * 
   * @return the http code, zero if exception not from a remote call
   */
  public int getHttpCode() {
    return httpCode;
  }

  /**
   * Gets the longer description, null if exception not from a remote call.
   * 
   * @return the description, null if exception not from a remote call
   */
  public String getDescription() {
    return description;
  }

}
