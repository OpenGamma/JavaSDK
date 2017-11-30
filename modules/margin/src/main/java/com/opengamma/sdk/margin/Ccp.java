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
 *
 * @deprecated Since 1.3.0. Replaced by an exact copy: {@link com.opengamma.sdk.margin.v3.Ccp}
 *   The current Enum will be removed in future versions.
 */
@Deprecated
public enum Ccp {

  /** Eurex. */
  EUREX,
  /** LCH. */
  LCH,
  /** CME. */
  CME,
  /** SIMM. */
  SIMM,
  /** JSCC. */
  JSCC,
  /** CME_SPAN. */
  CME_SPAN;
  ;

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
