/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static org.testng.Assert.assertEquals;

import java.time.Period;

import org.testng.annotations.Test;

/**
 * Test lenient period converter.
 */
@Test
public class TenorStringConverterTest {

  public void test() {
    TenorStringConverter test = new TenorStringConverter();
    assertEquals(test.convertFromString(Period.class, "P3M"), Period.ofMonths(3));
    assertEquals(test.convertFromString(Period.class, "3M"), Period.ofMonths(3));
    assertEquals(test.convertToString(Period.ofMonths(3)), "P3M");
  }

}
