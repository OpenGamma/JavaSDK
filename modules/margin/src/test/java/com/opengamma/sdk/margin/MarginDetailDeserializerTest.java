/*
 * Copyright (C) 2019 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * Test {@link MarginDetailDeserializer}.
 */
public class MarginDetailDeserializerTest {

  @Test
  public void testSupportedCcp() {
    assertTrue(MarginDetailDeserializer.of(Ccp.LCH).isPresent());
    assertTrue(MarginDetailDeserializer.of(Ccp.of("LCH_TEST")).isPresent());
  }

  @Test
  public void testUnsupportedCcp() {
    assertFalse(MarginDetailDeserializer.of(Ccp.EUREX).isPresent());
    assertFalse(MarginDetailDeserializer.of(Ccp.of("UNKNOWN")).isPresent());
  }
}
