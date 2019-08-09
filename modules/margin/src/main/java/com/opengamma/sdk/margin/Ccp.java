/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import org.joda.beans.JodaBeanUtils;
import org.joda.convert.FromString;

/**
 * Represents a CCP.
 */
public class Ccp implements Comparable<Ccp>, Serializable {

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

  /**
   * Returns an instance of {@code Ccp} corresponding to the given name.
   *
   * @param ccpName  the name of the CCP
   * @return an instance of {@code Ccp}
   */
  @FromString
  public static Ccp of(String ccpName) {
    JodaBeanUtils.notNull(ccpName, "ccpName");
    return new Ccp(ccpName.toUpperCase(Locale.ENGLISH));
  }

  /**
   * Returns all pre-defined CCPs.
   *
   * @return an array of CCPs
   * @deprecated there are no business cases that require this method, and it will be removed in a future version of the library.
   */
  @Deprecated
  public static Ccp[] values() {
    return new Ccp[]{EUREX, LCH, LCH_CDS, CME, SIMM, JSCC, CME_SPAN};
  }

  /**
   * Returns an instance of {@code Ccp} corresponding to the given name.
   *
   * @param ccpName  the name of the CCP
   * @return an instance of {@code Ccp}
   * @deprecated This method is added for backwards compatibility, and will be removed in a future version of the library.
   *  Please use `of()` instead
   */
  @Deprecated
  public static Ccp valueOf(String ccpName) {
    return of(ccpName);
  }

  private Ccp(String ccpName) {
    this.ccpName = ccpName;
  }

  private Ccp readResolve() {
    return of(ccpName);
  }

  /**
   * Returns the name of the CCP.
   *
   * @return the name of the CCP
   */
  public String name() {
    return this.ccpName;
  }

  @Override
  public int compareTo(Ccp o) {
    return this.name().compareTo(o.name());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ccp ccp = (Ccp) o;
    return Objects.equals(ccpName, ccp.ccpName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ccpName);
  }

  @Override
  public String toString() {
    return ccpName.toLowerCase(Locale.ENGLISH);
  }
}
