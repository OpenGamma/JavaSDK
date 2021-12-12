/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

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
@Disabled("API key not public")
@SuppressWarnings("deprecation")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MarginClientRemoteIT {
  // this test uses TestNG dependsOn so the individual tests are linked
  // this allows the latest valuation date to be tested without repeatedly calling getCcpInfo remotely

  private static final PortfolioDataFile PORTFOLIO =
      PortfolioDataFile.of(Paths.get("src/test/resources/lch-trades.txt"));

  private ServiceInvoker invoker;
  private MarginClient marginClient;
  private LocalDate valDate;

  //-------------------------------------------------------------------------
  @BeforeAll
  public void setUp() throws IOException {
    String apiKey = System.getenv("MARGIN_API_DEV_ID");
    String secret = System.getenv("MARGIN_API_DEV_SECRET");
    Credentials credentials = Credentials.ofApiKey(apiKey, secret);
    invoker = ServiceInvoker.builder(credentials)
        .serviceUrl(HttpUrl.parse("https://api.dev.opengamma.com"))
        .build();
    marginClient = MarginClient.of(invoker);
  }

  @AfterAll
  public void tearDown() throws IOException {
    if (invoker != null) {
      invoker.close();
    }
    marginClient = null;
  }

  //-------------------------------------------------------------------------
  @Test
  @Order(1)
  public void test_listCcps() {
    CcpsResult ccps = marginClient.listCcps();
    assertThat(ccps.getCcpNames().size() > 2).isTrue();
    assertThat(ccps.getCcpNames().contains(Ccp.LCH.toString())).isTrue();
    assertThat(ccps.getCcps().size() > 2).isTrue();
    assertThat(ccps.getCcps().contains(Ccp.LCH)).isTrue();
    assertThat(ccps.isCcpAvailable(Ccp.LCH)).isTrue();
  }

  @Test
  @Order(2)
  public void test_getCcpInfo() {
    CcpInfo ccpInfo = marginClient.getCcpInfo(Ccp.LCH);
    assertThat(ccpInfo.getReportingCurrencies().contains("GBP")).isTrue();
    assertThat(ccpInfo.getCalculationCurrencies().contains("GBP")).isTrue();
    assertThat(ccpInfo.getDefaultCurrency()).isEqualTo("GBP");
    assertThat(ccpInfo.getValuationDates().size() > 1).isTrue();
    assertThat(ccpInfo.getCalculationTypes().contains(MarginCalcType.PORTFOLIO_SUMMARY)).isTrue();
    assertThat(ccpInfo.getCalculationTypes().contains(MarginCalcType.MARGIN)).isTrue();
    assertThat(ccpInfo.getCalculationTypes().contains(MarginCalcType.MARGIN_DETAIL)).isTrue();
    assertThat(ccpInfo.getCalculationTypes().contains(MarginCalcType.PRESENT_VALUE)).isTrue();
    assertThat(ccpInfo.getCalculationTypes().contains(MarginCalcType.DELTA)).isTrue();
    assertThat(ccpInfo.getCalculationTypes().contains(MarginCalcType.GAMMA)).isTrue();
    valDate = ccpInfo.getLatestValuationDate();
  }

  //-------------------------------------------------------------------------
  @Test
  @Order(10)
  public void test_calculate_portfolioSummary() throws Exception {
    assumeTrue(valDate != null);
    MarginCalcRequest request = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.PORTFOLIO_SUMMARY)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertThat(result.getStatus()).isEqualTo(MarginCalcResultStatus.COMPLETED);
    assertThat(result.getType()).isEqualTo(MarginCalcRequestType.PARSE_INPUTS);
    assertThat(result.getCalculationTypes()).isEqualTo(set(MarginCalcType.PORTFOLIO_SUMMARY));
    assertThat(result.getValuationDate()).isEqualTo(valDate);
    assertThat(result.getReportingCurrency()).isEqualTo("GBP");
    assertThat(result.getCalculationCurrency()).isEqualTo("GBP");
    assertThat(result.getMode()).isEqualTo(MarginCalcMode.SPOT);
    assertThat(result.getPortfolioItems()).hasSize(4);
    assertThat(result.getMargin().isPresent()).isFalse();
    assertThat(result.getMarginDetail().isPresent()).isFalse();
    assertThat(result.getTradeValuations().isPresent()).isFalse();
  }

  @Test
  @Order(10)
  public void test_calculate_margin() throws Exception {
    assumeTrue(valDate != null);
    MarginCalcRequest request = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.MARGIN)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertThat(result.getStatus()).isEqualTo(MarginCalcResultStatus.COMPLETED);
    assertThat(result.getType()).isEqualTo(MarginCalcRequestType.STANDARD);
    assertThat(result.getCalculationTypes()).isEqualTo(set(MarginCalcType.MARGIN));
    assertThat(result.getValuationDate()).isEqualTo(valDate);
    assertThat(result.getReportingCurrency()).isEqualTo("GBP");
    assertThat(result.getCalculationCurrency()).isEqualTo("GBP");
    assertThat(result.getMode()).isEqualTo(MarginCalcMode.SPOT);
    assertThat(result.getPortfolioItems()).hasSize(0);
    assertThat(result.getMargin().isPresent()).isTrue();
    assertThat(result.getMarginDetail().isPresent()).isFalse();
    assertThat(result.getTradeValuations().isPresent()).isFalse();

    MarginSummary margin = result.getMargin().get();
    assertThat(margin.getMargin() != 0).isTrue();
    assertThat(margin.getMarginDetails().size() > 2).isTrue();
    assertThat(margin.getBreakdown().getTotalMargin()).isCloseTo(margin.getMargin(), offset(1e-8));
    assertThat(margin.getBreakdown().getBaseMargin() != 0).isTrue();
    assertThat(margin.getBreakdown().getAddOns() != 0).isTrue();
    assertThat(margin.getBreakdown().getNetLiquidatingValue()).isCloseTo(0, offset(1e-8));
  }

  @Test
  @Order(10)
  public void test_calculate_marginDetail() throws Exception {
    assumeTrue(valDate != null);
    MarginCalcRequest request = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.MARGIN_DETAIL)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertThat(result.getStatus()).isEqualTo(MarginCalcResultStatus.COMPLETED);
    assertThat(result.getType()).isEqualTo(MarginCalcRequestType.STANDARD);
    assertThat(result.getCalculationTypes()).isEqualTo(set(MarginCalcType.MARGIN_DETAIL));
    assertThat(result.getValuationDate()).isEqualTo(valDate);
    assertThat(result.getReportingCurrency()).isEqualTo("GBP");
    assertThat(result.getCalculationCurrency()).isEqualTo("GBP");
    assertThat(result.getMode()).isEqualTo(MarginCalcMode.SPOT);
    assertThat(result.getPortfolioItems()).hasSize(0);
    assertThat(result.getMargin().isPresent()).isFalse();
    assertThat(result.getMarginDetail().isPresent()).isTrue();
    assertThat(result.getTradeValuations().isPresent()).isFalse();

    LchMarginDetail margin = (LchMarginDetail) result.getMarginDetail().get();
    assertThat(margin.getCcp()).isEqualTo(Ccp.LCH);
    assertThat(margin.getTotalMargin() != 0).isTrue();
    assertThat(margin.getBaseScenarioIds()).hasSize(6);
    assertThat(margin.getIndices().size() > 2).isTrue();
    LchMarginIndex index = margin.getIndices().get(0);
    assertThat(index.getIndexName().length() > 4).isTrue();
    assertThat(index.getDiversifiedBaseMargin() != 0).isTrue();
    assertThat(index.getUndiversifiedBaseMargin() != 0).isTrue();
    assertThat(index.getIndexScenarioIds()).hasSize(6);
    assertThat(margin.getScenarios().size() >= 6).isTrue();
    LchMarginScenario scenario = margin.getScenarios().get(0);
    assertThat(scenario.getId().length() > 0).isTrue();
    assertThat(scenario.getScaledPortfolioPnl() != 0).isTrue();
    assertThat(scenario.getUnscaledPortfolioPnl() != 0).isTrue();
  }

  @Test
  @Order(10)
  public void test_calculate_pv() throws Exception {
    assumeTrue(valDate != null);
    MarginCalcRequest request = MarginCalcRequest.builder()
        .calculationTypes(MarginCalcType.PRESENT_VALUE, MarginCalcType.DELTA)
        .valuationDate(valDate)
        .reportingCurrency("GBP")
        .portfolioData(PORTFOLIO)
        .build();
    MarginCalcResult result = marginClient.calculate(Ccp.LCH, request);
    assertThat(result.getStatus()).isEqualTo(MarginCalcResultStatus.COMPLETED);
    assertThat(result.getType()).isEqualTo(MarginCalcRequestType.STANDARD);
    assertThat(result.getCalculationTypes()).isEqualTo(set(MarginCalcType.PRESENT_VALUE, MarginCalcType.DELTA));
    assertThat(result.getValuationDate()).isEqualTo(valDate);
    assertThat(result.getReportingCurrency()).isEqualTo("GBP");
    assertThat(result.getCalculationCurrency()).isEqualTo("GBP");
    assertThat(result.getMode()).isEqualTo(MarginCalcMode.SPOT);
    assertThat(result.getPortfolioItems()).hasSize(0);
    assertThat(result.getMargin().isPresent()).isFalse();
    assertThat(result.getMarginDetail().isPresent()).isFalse();
    assertThat(result.getTradeValuations().isPresent()).isTrue();

    TradeValuations vals = result.getTradeValuations().get();
    assertThat(vals.getTotalPresentValue() != 0).isTrue();
    assertThat(vals.getTotalDelta().isPresent()).isTrue();
    assertThat(vals.getBucketedDelta().isPresent()).isTrue();
    assertThat(vals.getTotalGamma().isPresent()).isFalse();
    assertThat(vals.getBucketedGamma().isPresent()).isFalse();
    assertThat(vals.getTotalDelta().getAsDouble() != 0).isTrue();
    assertThat(vals.getBucketedDelta().get()).hasSize(3);
    assertThat(vals.getTrades()).hasSize(4);
    TradeValuation val = vals.getTrades().get(0);
    assertThat(val.getValue().isPresent()).isTrue();
    assertThat(val.getDelta().isPresent()).isTrue();
    assertThat(val.getGamma().isPresent()).isFalse();
    TradeValue value = val.getValue().get();
    assertThat(value.getPresentValue() != 0).isTrue();
    assertThat(value.getTradeCurrency().length()).isEqualTo(3);
    assertThat(value.getPresentValueTradeCurrency() != 0).isTrue();
    TradeSensitivity delta = val.getDelta().get();
    assertThat(delta.getSensitivity() != 0).isTrue();
    assertThat(delta.getCurveSensitivity().size() > 0).isTrue();
    TradeCurveSensitivity curve = delta.getCurveSensitivity().get(0);
    assertThat(curve.getCurrency().length()).isEqualTo(3);
    assertThat(curve.getCurveName().length() > 4).isTrue();
    assertThat(curve.getSensitivity() != 0).isTrue();
    assertThat(curve.getTenorSensitivity().size() > 4).isTrue();
    assertThat(curve.getTenorSensitivity().get(Period.ofMonths(12)) != 0).isTrue();
  }

  //-------------------------------------------------------------------------
  @SafeVarargs
  private static final <T> Set<T> set(T... array) {
    return new HashSet<>(Arrays.asList(array));
  }

}
