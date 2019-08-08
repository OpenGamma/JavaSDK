/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.util.Locale;

import org.joda.convert.FromString;

/**
 * Represents a CCP.
 */
public class Ccp  {

  /** Eurex. */
  public static final Ccp EUREX = new Ccp("EUREX");
  /** LCH. */
  public static final Ccp LCH = new Ccp("LCH");
  /** LCH CDS. */
  public static final Ccp LCH_CDS = new Ccp("LCH_CDS");
  /** CME. */
  public static final Ccp CME = new Ccp("CME");
  /** SIMM. */
  public static final Ccp SIMM = new Ccp("SIMM");
  /** JSCC. */
  public static final Ccp JSCC = new Ccp("JSCC");
  /** CME_SPAN. */
  public static final Ccp CME_SPAN = new Ccp("CME_SPAN");

  private final String ccpName;

  private Ccp(String ccpName) {
    this.ccpName = ccpName;
  }

  /**
   * Returns an instance of {@link Ccp} corresponding to the given name.
   *
   * @param ccpName the name of the CCP
   * @return an instance of {@link Ccp}
   */
  @FromString
  public static Ccp of(String ccpName) {
    return new Ccp(ccpName.toUpperCase(Locale.ENGLISH));
  }

  /**
   * Returns the name of the Ccp.
   *
   * @return the name of the Ccp
   */
  public String name() {
    return this.toString();
  }

  /**
   * Returns an instance of {@link Ccp} corresponding to the given name.
   *
   * This method is added for backwards compatibility, and will be removed in a future version of the library.
   * Please use `of()` instead
   * @param ccpName the name of the CCP
   * @return an instance of {@link Ccp}
   */
  @Deprecated
  public static Ccp valueOf(String ccpName) {
    return of(ccpName);
  }

  @Override
  public String toString() {
    return ccpName.toUpperCase(Locale.ENGLISH);
  }
}
