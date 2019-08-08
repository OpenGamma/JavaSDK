/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

/**
 * Test {@link Version}.
 */
@Test
public class VersionTest {

  public void test_version() {
    assertFalse(Version.getVersionString().isEmpty());
    // this line fails when tests are run in IntelliJ (works in Eclipse)
    // assertEquals(Version.getVersionString().contains("$"), false);
  }

}
