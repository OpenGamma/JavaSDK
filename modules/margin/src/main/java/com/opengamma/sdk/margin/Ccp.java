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

  /** ASX (Australian Securities Exchange) SPAN. */
  public static final Ccp ASX_SPAN= new Ccp("ASX_SPAN");
  /** BMD (Bursa Malaysia) SPAN. */
  public static final Ccp BMD_SPAN = new Ccp("BMD_SPAN");
  /** CBOE (Chicago Board Options Exchange) SPAN. */
  public static final Ccp CBOE_SPAN = new Ccp("CBOE_SPAN");
  /** CDCC (Canadian Derivatives Clearing Corporation) SPAN. */
  public static final Ccp CDCC_SPAN = new Ccp("CDCC_SPAN");
  /** CME (Chicago Mercantile Exchange). */
  public static final Ccp CME = new Ccp("CME");
  /** CME (Chicago Mercantile Exchange) SPAN. */
  public static final Ccp CME_SPAN = new Ccp("CME_SPAN");
  /** ECC (European Commodity Clearing) SPAN. */
  public static final Ccp ECC_SPAN = new Ccp("ECC_SPAN");
  /** Eurex. */
  public static final Ccp EUREX = new Ccp("EUREX");
  /** LCH. */
  public static final Ccp LCH = new Ccp("LCH");
  /** LCH CDS. */
  public static final Ccp LCH_CDS = new Ccp("LCH_CDS");
  /** HKEX (Hong Kong Exchange) SPAN. */
  public static final Ccp HKEX_SPAN = new Ccp("HKEX_SPAN");
  /** ICE (Intercontinental Exchange) SPAN. */
  public static final Ccp ICE_SPAN = new Ccp("ICE_SPAN");
  /** JCCH (Japan Commodity Clearing House) SPAN. */
  public static final Ccp JCCH_SPAN = new Ccp("JCCH_SPAN");
  /** JSCC (Japan Securities Clearing Corp). */
  public static final Ccp JSCC = new Ccp("JSCC");
  /** JSCC (Japan Securities Clearing Corp) SPAN. */
  public static final Ccp JSCC_SPAN= new Ccp("JSCC_SPAN");
  /** LME (London Metal Exchange) SPAN. */
  public static final Ccp LME_SPAN = new Ccp("LME_SPAN");
  /** MGE (Minneapolis Grain Exchange) SPAN. */
  public static final Ccp MGE_SPAN = new Ccp("MGE_SPAN");
  /** SGX (Singapore Exchange) SPAN. */
  public static final Ccp SGX_SPAN= new Ccp("SGX_SPAN");
  /** SIMM. */
  public static final Ccp SIMM = new Ccp("SIMM");
  /** TIF (Tokyo Financial Exchange) SPAN. */
  public static final Ccp TIF_SPAN = new Ccp("TIF_SPAN");

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
    return new Ccp[]{ASX_SPAN, BMD_SPAN, CBOE_SPAN, CDCC_SPAN, CME, CME_SPAN, ECC_SPAN, EUREX, LCH, LCH_CDS, HKEX_SPAN, ICE_SPAN, JCCH_SPAN, JSCC, JSCC_SPAN, LME_SPAN, MGE_SPAN, SGX_SPAN, SIMM, TIF_SPAN};
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
