/*
 * Copyright (C) 2019 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test {@link MarginDetailDeserializer}.
 */
public class MarginDetailDeserializerTest {

  @Test
  public void testSupportedCcp() {
    assertThat(MarginDetailDeserializer.of(Ccp.LCH).isPresent()).isTrue();
    assertThat(MarginDetailDeserializer.of(Ccp.of("LCH_TEST")).isPresent()).isTrue();
  }

  @Test
  public void testUnsupportedCcp() {
    assertThat(MarginDetailDeserializer.of(Ccp.EUREX).isPresent()).isFalse();
    assertThat(MarginDetailDeserializer.of(Ccp.of("UNKNOWN")).isPresent()).isFalse();
  }
}
