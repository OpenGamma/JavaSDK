/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

/**
 * The types of calculation that can be performed.
 * <p>
 * This enumerates the different kinds of calculation the service can perform.
 * Note that not all CCPs support all calculations and some calculations require additional permissions.
 */
public enum MarginCalcType {
  // this is a fixed list, which might cause a problem in deserialization if the server sends back a new value
  // but the server will only send a value back if it is requested by the client
  // since an older client won't request a newer value, also won't receive a newer value

  /**
   * Requests the portfolio summary.
   * This can be used to check that the portfolio was parsed correctly.
   */
  PORTFOLIO_SUMMARY,

  /**
   * Requests the margin.
   */
  MARGIN,

  /**
   * Requests the detailed breakdown of the margin.
   */
  MARGIN_DETAIL,

  /**
   * Requests the present value.
   */
  PRESENT_VALUE,

  /**
   * Requests the delta of the present value sensitivity.
   */
  DELTA,

  /**
   * Requests the gamma of the present value sensitivity.
   */
  GAMMA,

}
