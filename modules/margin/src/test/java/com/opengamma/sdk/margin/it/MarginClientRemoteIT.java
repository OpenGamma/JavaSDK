/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.it;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;
import com.opengamma.sdk.margin.Ccp;
import com.opengamma.sdk.margin.CcpInfo;
import com.opengamma.sdk.margin.CcpsResult;
import com.opengamma.sdk.margin.MarginCalcMode;
import com.opengamma.sdk.margin.MarginCalcRequest;
import com.opengamma.sdk.margin.MarginCalcRequestType;
import com.opengamma.sdk.margin.MarginCalcResult;
import com.opengamma.sdk.margin.MarginCalcResultStatus;
import com.opengamma.sdk.margin.MarginClient;
import com.opengamma.sdk.margin.MarginSummary;
import com.opengamma.sdk.margin.PortfolioDataFile;

import okhttp3.HttpUrl;

/**
 * Tests the API against the dev system.
 * Run as a formal integration test via maven failsafe.
 * Requires two environment variables, hence is run via a maven profile.
 */
@Test
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
    String apiKey = System.getenv("MARGIN_API_KEY");
    String secret = System.getenv("MARGIN_API_SECRET");
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
    assertEquals(ccps.isCcpAvailable(Ccp.LCH), true);
  }

  @Test(dependsOnMethods = "test_listCcps")
  public void test_getCcpInfo() {
    CcpInfo ccpInfo = marginClient.getCcpInfo(Ccp.LCH);
    assertTrue(ccpInfo.getReportingCurrencies().contains("GBP"));
    assertTrue(ccpInfo.getCalculationCurrencies().contains("GBP"));
    assertEquals(ccpInfo.getDefaultCurrency(), "GBP");
    assertTrue(ccpInfo.getValuationDates().size() > 1);
    valDate = ccpInfo.getLatestValuationDate();
  }

  //-------------------------------------------------------------------------
  @Test(dependsOnMethods = "test_getCcpInfo")
  public void test_calculate_portfolioSummary() throws Exception {
    assert valDate != null;
    MarginCalcRequest request = MarginCalcRequest.builder()
        .type(MarginCalcRequestType.PARSE_INPUTS)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.PARSE_INPUTS);
    assertEquals(result.getValuationDate(), valDate);
    assertEquals(result.getReportingCurrency(), "GBP");
    assertEquals(result.getMode(), MarginCalcMode.SPOT);
    assertEquals(result.getPortfolioItems().size(), 4);
    assertEquals(result.getMargin().isPresent(), false);
  }

  @Test(dependsOnMethods = "test_getCcpInfo")
  public void test_calculate_margin() throws Exception {
    assert valDate != null;
    MarginCalcRequest request = MarginCalcRequest.builder()
        .type(MarginCalcRequestType.STANDARD)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getValuationDate(), valDate);
    assertEquals(result.getReportingCurrency(), "GBP");
    assertEquals(result.getMode(), MarginCalcMode.SPOT);
    assertEquals(result.getPortfolioItems().size(), 0);
    assertEquals(result.getMargin().isPresent(), true);

    MarginSummary margin = result.getMargin().get();
    assertTrue(margin.getMargin() != 0);
    assertTrue(margin.getMarginDetails().size() > 2);
  }

}
