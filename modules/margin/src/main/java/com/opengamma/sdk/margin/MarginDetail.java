/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import org.joda.beans.ImmutableBean;

/**
 * A detailed breakdown of the margin calculation.
 */
public abstract class MarginDetail implements ImmutableBean {
  // this implements ImmutableBean so that the Joda-Beans deserialization code kicks in
  // and uses MarginDetailDeserializer to select the correct subtype

  // restrict construction to this package
  MarginDetail() {
  }

  /**
   * Gets the CCP.
   * 
   * @return the CCP
   */
  public abstract Ccp getCcp();

}
