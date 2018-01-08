/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

/**
 * Provides a list of Margin Service operations available in the SDK
 */
public enum MarginOperation {
  /** List CCPs operation */
  LIST_CCPS("List CCPs"),

  /** Get CCPs information operation */
  GET_CCP_INFO("Get CCP Information"),

  /** Create calculation operation */
  CREATE_CALCULATION("Create calculation"),

  /** Get calculation operation */
  GET_CALCULATION("Get calculation"),

  /** Delete calculation operation */
  DELETE_CALCULATION("Delete calculation");

  /**
   * Provides a user-readable name of the Margin Service operation
   */
  private final String description;

  /**
   * Constructs an instance for each item in this ENUM, together with its user-friendly version.
   * @param description  the user-readable version of the Margin Service operation
   */
  MarginOperation(String description) {
    this.description = description;
  }

  String getDescription() {
    return description;
  }
}
