package com.opengamma.sdk.margin;

/**
 * Provides a list of Margin Service operations available in the SDK
 */
public enum MarginOperation {
  LIST_CCPS("List CCPs"),
  GET_CCP_INFO("Get CCP Information"),
  CREATE_CALCULATION("Create calculation"),
  GET_CALCULATION("Get calculation"),
  DELETE_CALCULATION("Delete calculation");

  /**
   * Provides a user-readable name of the Margin Service operation
   */
  private final String userFriendlyName;

  /**
   * Constructs an instance for each item in this ENUM, together with its user-friendly version.
   * @param userFriendlyName  the user-readable version of the Margin Service operation
   */
  MarginOperation(String userFriendlyName) {
    this.userFriendlyName = userFriendlyName;
  }

  String getUserFriendlyName() {
    return userFriendlyName;
  }
}
