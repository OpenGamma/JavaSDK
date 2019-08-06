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

  @FromString
  public static Ccp of(String ccpName) {
    return new Ccp(ccpName.toUpperCase(Locale.ENGLISH));
  }

  public String name() {
    return this.toString();
  }

  @Override
  public String toString() {
    return ccpName.toUpperCase(Locale.ENGLISH);
  }
}
