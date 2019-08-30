/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Period;

import org.junit.jupiter.api.Test;

/**
 * Test lenient period converter.
 */
public class TenorStringConverterTest {

  @Test
  public void test() {
    TenorStringConverter test = new TenorStringConverter();
    assertThat(test.convertFromString(Period.class, "P3M")).isEqualTo(Period.ofMonths(3));
    assertThat(test.convertFromString(Period.class, "3M")).isEqualTo(Period.ofMonths(3));
    assertThat(test.convertToString(Period.ofMonths(3))).isEqualTo("P3M");
  }

}
