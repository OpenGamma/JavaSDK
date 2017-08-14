/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

/**
 * The status of the calculation.
 *
 * @deprecated Since 1.3.0. Replaced by an exact copy: {@link com.opengamma.sdk.margin.v3.MarginCalcResultStatus}.
 *   The current Enum will be removed in future versions.
 */
@Deprecated
public enum MarginCalcResultStatus {

  /**
   * Calculation is pending.
   */
  PENDING,
  /**
   * Calculation has completed.
   */
  COMPLETED;

}
