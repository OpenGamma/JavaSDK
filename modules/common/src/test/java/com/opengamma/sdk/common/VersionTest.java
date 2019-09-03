/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Version}.
 */
public class VersionTest {

  @Test
  public void test_version() {
    assertThat(Version.getVersionString().isEmpty()).isFalse();
    // this line fails when tests are run in IntelliJ (works in Eclipse)
    // assertEquals(Version.getVersionString().contains("$"), false);
  }

}
