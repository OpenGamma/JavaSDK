/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static com.opengamma.sdk.margin.MarginCalcRequestType.PARSE_INPUTS;
import static org.testng.Assert.assertEquals;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;

import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class MarginCalcRequestTest {

  private static final LocalDate VAL_DATE = LocalDate.of(2017, 6, 1);
  private static final PortfolioDataFile PORTFOLIO =
      PortfolioDataFile.of(Paths.get("src/test/resources/lch-trades.txt"));

  //-------------------------------------------------------------------------
  @SuppressWarnings("deprecation")
  public void test_of3() {
    MarginCalcRequest test = MarginCalcRequest.of(VAL_DATE, "GBP", Arrays.asList(PORTFOLIO));
    MarginCalcRequest expected = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.MARGIN)
        .mode(MarginCalcMode.SPOT)
        .valuationDate(VAL_DATE)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    assertEquals(test, expected);
    assertEquals(test.getType(), MarginCalcRequestType.STANDARD);
  }

  @SuppressWarnings("deprecation")
  public void test_of4() {
    MarginCalcRequest test = MarginCalcRequest.of(VAL_DATE, "GBP", Arrays.asList(PORTFOLIO), "MYPARTY");
    MarginCalcRequest expected = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.MARGIN)
        .mode(MarginCalcMode.SPOT)
        .valuationDate(VAL_DATE)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .fpmlPartySelectionRegex("MYPARTY")
        .build();
    assertEquals(test, expected);
    assertEquals(test.getType(), MarginCalcRequestType.STANDARD);
  }

  @SuppressWarnings("deprecation")
  public void test_of5() {
    MarginCalcRequest test = MarginCalcRequest.of(VAL_DATE, "GBP", Arrays.asList(PORTFOLIO), PARSE_INPUTS, true);
    MarginCalcRequest expected = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.PORTFOLIO_SUMMARY)
        .mode(MarginCalcMode.SPOT)
        .valuationDate(VAL_DATE)
        .reportingCurrency("GBP")
        .applyClientMultiplier(true)
        .portfolioData(PORTFOLIO)
        .build();
    assertEquals(test, expected);
    assertEquals(test.getType(), MarginCalcRequestType.PARSE_INPUTS);
  }

}
