/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

/**
 * The type of margin calculation to perform.
 */
public enum MarginCalcRequestType {

  /**
   * Parse and summarize the inputs, performing no margin calculation.
   * <p>
   * Portfolio summary and parsing errors will be returned.
   */
  PARSE_INPUTS,
  /**
   * Perform a standard margin calculation for the inputs.
   * <p>
   * Margin, parsing errors and calculation errors will be returned.
   */
  STANDARD,
  /**
   * Perform a full margin calculation for the inputs.
   * <p>
   * Margin, portfolio summary, parsing errors and calculation errors will be returned.
   */
  FULL;

}
