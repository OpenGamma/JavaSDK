/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.util.Locale;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * Represents a CCP.
 */
public enum Ccp {

  /** Eurex. */
  EUREX,
  /** LCH. */
  LCH,
  /** CME. */
  CME,
  /** SIMM. */
  SIMM;

  @FromString
  public static final Ccp of(String str) {
    return valueOf(str.toUpperCase(Locale.ENGLISH));
  }

  @Override
  @ToString
  public String toString() {
    return super.toString().toLowerCase(Locale.ENGLISH);
  }

}
