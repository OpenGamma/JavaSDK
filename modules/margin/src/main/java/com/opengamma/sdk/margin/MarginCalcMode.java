package com.opengamma.sdk.margin;

/**
 * The mode in which the calculation will be executed.
 */
public enum MarginCalcMode {

  /**
   * Runs the margin calculation in Spot mode.
   */
  SPOT,

  /**
   * Runs the margin calculation in Forward Approximation mode.
   */
  FORWARD

}