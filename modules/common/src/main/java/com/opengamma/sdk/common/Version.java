/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import java.io.InputStream;
import java.util.Properties;

/**
 * Provides access to the version of the SDK.
 */
public class Version {

  /**
   * The version, which will be populated by the Maven build.
   */
  private static final String VERSION;

  /**
   * This block is loading the version value from the version.properties method.
   */
  static {
    Properties properties = new Properties();
    String version;
    try {
      InputStream resourceAsStream = Version.class.getResourceAsStream("version.properties");
      properties.load(resourceAsStream);
      version = properties.getProperty("version");
    } catch (Exception e) {
      version = "";
    }
    VERSION = version;
  }

  /**
   * Restricted constructor.
   */
  private Version() {
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the version of Strata.
   *
   * @return the version
   */
  public static String getVersionString() {
    return VERSION;
  }

}
