/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.it;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;
import com.opengamma.sdk.margin.Ccp;
import com.opengamma.sdk.margin.CcpInfo;
import com.opengamma.sdk.margin.CcpsResult;
import com.opengamma.sdk.margin.LchMarginDetail;
import com.opengamma.sdk.margin.LchMarginIndex;
import com.opengamma.sdk.margin.LchMarginScenario;
import com.opengamma.sdk.margin.MarginCalcMode;
import com.opengamma.sdk.margin.MarginCalcRequest;
import com.opengamma.sdk.margin.MarginCalcRequestType;
import com.opengamma.sdk.margin.MarginCalcResult;
import com.opengamma.sdk.margin.MarginCalcResultStatus;
import com.opengamma.sdk.margin.MarginCalcType;
import com.opengamma.sdk.margin.MarginClient;
import com.opengamma.sdk.margin.MarginSummary;
import com.opengamma.sdk.margin.PortfolioDataFile;
import com.opengamma.sdk.margin.TradeCurveSensitivity;
import com.opengamma.sdk.margin.TradeSensitivity;
import com.opengamma.sdk.margin.TradeValuation;
import com.opengamma.sdk.margin.TradeValuations;
import com.opengamma.sdk.margin.TradeValue;

import okhttp3.HttpUrl;

/**
 * Tests the API against the dev system.
 * Run as a formal integration test via maven failsafe.
 * Requires two environment variables, hence is run via a maven profile.
 */
@Test
@SuppressWarnings("deprecation")
public class MarginClientRemoteIT {
  // this test uses TestNG dependsOn so the individual tests are linked
  // this allows the latest valuation date to be tested without repeatedly calling getCcpInfo remotely

  private static final PortfolioDataFile PORTFOLIO =
      PortfolioDataFile.of(Paths.get("src/test/resources/lch-trades.txt"));

  private ServiceInvoker invoker;
  private MarginClient marginClient;
  private LocalDate valDate;

  //-------------------------------------------------------------------------
  @BeforeClass
  public void setUp() throws IOException {
    String apiKey = System.getenv("MARGIN_API_DEV_ID");
    String secret = System.getenv("MARGIN_API_DEV_SECRET");
    Credentials credentials = Credentials.ofApiKey(apiKey, secret);
    invoker = ServiceInvoker.builder(credentials)
        .serviceUrl(HttpUrl.parse("https://api.dev.opengamma.com"))
        .build();
    marginClient = MarginClient.of(invoker);
  }

  @AfterClass
  public void tearDown() throws IOException {
    invoker.close();
    marginClient = null;
  }

  //-------------------------------------------------------------------------
  public void test_listCcps() {
    CcpsResult ccps = marginClient.listCcps();
    assertTrue(ccps.getCcpNames().size() > 2);
    assertTrue(ccps.getCcpNames().contains(Ccp.LCH.toString()));
    assertTrue(ccps.getCcps().size() > 2);
    assertTrue(ccps.getCcps().contains(Ccp.LCH));
    assertTrue(ccps.isCcpAvailable(Ccp.LCH));
  }

  @Test(dependsOnMethods = "test_listCcps")
  public void test_getCcpInfo() {
    CcpInfo ccpInfo = marginClient.getCcpInfo(Ccp.LCH);
    assertTrue(ccpInfo.getReportingCurrencies().contains("GBP"));
    assertTrue(ccpInfo.getCalculationCurrencies().contains("GBP"));
    assertEquals(ccpInfo.getDefaultCurrency(), "GBP");
    assertTrue(ccpInfo.getValuationDates().size() > 1);
    assertTrue(ccpInfo.getCalculationTypes().contains(MarginCalcType.PORTFOLIO_SUMMARY));
    assertTrue(ccpInfo.getCalculationTypes().contains(MarginCalcType.MARGIN));
    assertTrue(ccpInfo.getCalculationTypes().contains(MarginCalcType.MARGIN_DETAIL));
    assertTrue(ccpInfo.getCalculationTypes().contains(MarginCalcType.PRESENT_VALUE));
    assertTrue(ccpInfo.getCalculationTypes().contains(MarginCalcType.DELTA));
    assertTrue(ccpInfo.getCalculationTypes().contains(MarginCalcType.GAMMA));
    valDate = ccpInfo.getLatestValuationDate();
  }

  //-------------------------------------------------------------------------
  @Test(dependsOnMethods = "test_getCcpInfo")
  public void test_calculate_portfolioSummary() throws Exception {
    assert valDate != null;
    MarginCalcRequest request = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.PORTFOLIO_SUMMARY)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.PARSE_INPUTS);
    assertEquals(result.getCalculationTypes(), set(MarginCalcType.PORTFOLIO_SUMMARY));
    assertEquals(result.getValuationDate(), valDate);
    assertEquals(result.getReportingCurrency(), "GBP");
    assertEquals(result.getCalculationCurrency(), "GBP");
    assertEquals(result.getMode(), MarginCalcMode.SPOT);
    assertEquals(result.getPortfolioItems().size(), 4);
    assertFalse(result.getMargin().isPresent());
    assertFalse(result.getMarginDetail().isPresent());
    assertFalse(result.getTradeValuations().isPresent());
  }

  @Test(dependsOnMethods = "test_getCcpInfo")
  public void test_calculate_margin() throws Exception {
    assert valDate != null;
    MarginCalcRequest request = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.MARGIN)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getCalculationTypes(), set(MarginCalcType.MARGIN));
    assertEquals(result.getValuationDate(), valDate);
    assertEquals(result.getReportingCurrency(), "GBP");
    assertEquals(result.getCalculationCurrency(), "GBP");
    assertEquals(result.getMode(), MarginCalcMode.SPOT);
    assertEquals(result.getPortfolioItems().size(), 0);
    assertTrue(result.getMargin().isPresent());
    assertFalse(result.getMarginDetail().isPresent());
    assertFalse(result.getTradeValuations().isPresent());

    MarginSummary margin = result.getMargin().get();
    assertTrue(margin.getMargin() != 0);
    assertTrue(margin.getMarginDetails().size() > 2);
    assertEquals(margin.getBreakdown().getTotalMargin(), margin.getMargin(), 1e-8);
    assertTrue(margin.getBreakdown().getBaseMargin() != 0);
    assertTrue(margin.getBreakdown().getAddOns() != 0);
    assertEquals(margin.getBreakdown().getNetLiquidatingValue(), 0, 1e-8);
  }

  @Test(dependsOnMethods = "test_getCcpInfo")
  public void test_calculate_marginDetail() throws Exception {
    assert valDate != null;
    MarginCalcRequest request = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.MARGIN_DETAIL)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getCalculationTypes(), set(MarginCalcType.MARGIN_DETAIL));
    assertEquals(result.getValuationDate(), valDate);
    assertEquals(result.getReportingCurrency(), "GBP");
    assertEquals(result.getCalculationCurrency(), "GBP");
    assertEquals(result.getMode(), MarginCalcMode.SPOT);
    assertEquals(result.getPortfolioItems().size(), 0);
    assertFalse(result.getMargin().isPresent());
    assertTrue(result.getMarginDetail().isPresent());
    assertFalse(result.getTradeValuations().isPresent());

    LchMarginDetail margin = (LchMarginDetail) result.getMarginDetail().get();
    assertEquals(margin.getCcp(), Ccp.LCH);
    assertTrue(margin.getTotalMargin() != 0);
    assertEquals(margin.getBaseScenarioIds().size(), 6);
    assertTrue(margin.getIndices().size() > 2);
    LchMarginIndex index = margin.getIndices().get(0);
    assertTrue(index.getIndexName().length() > 4);
    assertTrue(index.getDiversifiedBaseMargin() != 0);
    assertTrue(index.getUndiversifiedBaseMargin() != 0);
    assertEquals(index.getIndexScenarioIds().size(), 6);
    assertTrue(margin.getScenarios().size() >= 6);
    LchMarginScenario scenario = margin.getScenarios().get(0);
    assertTrue(scenario.getId().length() > 0);
    assertTrue(scenario.getScaledPortfolioPnl() != 0);
    assertTrue(scenario.getUnscaledPortfolioPnl() != 0);
  }

  @Test(dependsOnMethods = "test_getCcpInfo")
  public void test_calculate_pv() throws Exception {
    assert valDate != null;
    MarginCalcRequest request = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.PRESENT_VALUE, MarginCalcType.DELTA)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getCalculationTypes(), set(MarginCalcType.PRESENT_VALUE, MarginCalcType.DELTA));
    assertEquals(result.getValuationDate(), valDate);
    assertEquals(result.getReportingCurrency(), "GBP");
    assertEquals(result.getCalculationCurrency(), "GBP");
    assertEquals(result.getMode(), MarginCalcMode.SPOT);
    assertEquals(result.getPortfolioItems().size(), 0);
    assertFalse(result.getMargin().isPresent());
    assertFalse(result.getMarginDetail().isPresent());
    assertTrue(result.getTradeValuations().isPresent());

    TradeValuations vals = result.getTradeValuations().get();
    assertTrue(vals.getTotalPresentValue() != 0);
    assertTrue(vals.getTotalDelta().isPresent());
    assertTrue(vals.getBucketedDelta().isPresent());
    assertFalse(vals.getTotalGamma().isPresent());
    assertFalse(vals.getBucketedGamma().isPresent());
    assertTrue(vals.getTotalDelta().getAsDouble() != 0);
    assertEquals(vals.getBucketedDelta().get().size(), 3);
    assertEquals(vals.getTrades().size(), 4);
    TradeValuation val = vals.getTrades().get(0);
    assertTrue(val.getValue().isPresent());
    assertTrue(val.getDelta().isPresent());
    assertFalse(val.getGamma().isPresent());
    TradeValue value = val.getValue().get();
    assertTrue(value.getPresentValue() != 0);
    assertEquals(value.getTradeCurrency().length(), 3);
    assertTrue(value.getPresentValueTradeCurrency() != 0);
    TradeSensitivity delta = val.getDelta().get();
    assertTrue(delta.getSensitivity() != 0);
    assertTrue(delta.getCurveSensitivity().size() > 0);
    TradeCurveSensitivity curve = delta.getCurveSensitivity().get(0);
    assertEquals(curve.getCurrency().length(), 3);
    assertTrue(curve.getCurveName().length() > 4);
    assertTrue(curve.getSensitivity() != 0);
    assertTrue(curve.getTenorSensitivity().size() > 4);
    assertTrue(curve.getTenorSensitivity().get(Period.ofMonths(12)) != 0);
  }

  //-------------------------------------------------------------------------
  @SafeVarargs
  private static final <T> Set<T> set(T... array) {
    return new HashSet<>(Arrays.asList(array));
  }

}
