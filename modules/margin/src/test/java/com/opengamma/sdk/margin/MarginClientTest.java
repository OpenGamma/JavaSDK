/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import org.joda.beans.ser.JodaBeanSer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

/**
 * Test.
 */
@Test
public class MarginClientTest {

  private static final Credentials CREDENTIALS = Credentials.ofApiKey("user", "password");
  private static final LocalDate VAL_DATE = LocalDate.of(2017, 6, 1);
  private static final MarginCalcRequest REQUEST = MarginCalcRequest.builder()
      .calculationTypes(MarginCalcType.MARGIN)
      .valuationDate(VAL_DATE)
      .reportingCurrency("GBP")
      .build();

  private static final String RESPONSE_LIST_CCPS = JodaBeanSer.PRETTY.simpleJsonWriter()
      .write(CcpsResult.of(Arrays.asList("LCH", "RUBBISH")));

  private static final String RESPONSE_GET_CCP_INFO = JodaBeanSer.PRETTY.simpleJsonWriter()
      .write(CcpInfo.of(
          Collections.singletonList(VAL_DATE),
          "GBP",
          Collections.singletonList("GBP"),
          Collections.singletonList("GBP"),
          new HashSet<>(Arrays.asList(MarginCalcType.PORTFOLIO_SUMMARY, MarginCalcType.MARGIN)),
          new HashSet<>(Arrays.asList(MarginCalcMode.SPOT))));

  private static final String RESPONSE_CALC_POST = "";
  private static final String RESPONSE_CALC_GET_PENDING = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      MarginCalcResult.of(
          MarginCalcResultStatus.PENDING,
          set(MarginCalcType.MARGIN),
          MarginCalcMode.SPOT,
          VAL_DATE,
          "GBP",
          "GBP",
          true,
          Collections.emptyList(),
          null,
          null,
          null,
          Collections.emptyList()));
  private static final String RESPONSE_CALC_GET_COMPLETE = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      MarginCalcResult.of(
          MarginCalcResultStatus.COMPLETED,
          set(MarginCalcType.MARGIN),
          MarginCalcMode.SPOT,
          VAL_DATE,
          "GBP",
          "GBP",
          true,
          Collections.singletonList(PortfolioItemSummary.of("1", "SWAP", "MySwap")),
          MarginSummary.of(125d, Collections.emptyList(), MarginBreakdown.of(125d, 125d, 0, 0)),
          null,
          null,
          Collections.emptyList()));
  private static final String RESPONSE_CALC_WHATIF_GET_COMPLETE = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      MarginCalcResult.of(
          MarginCalcResultStatus.COMPLETED,
          set(MarginCalcType.MARGIN),
          MarginCalcMode.SPOT,
          VAL_DATE,
          "GBP",
          "GBP",
          true,
          Collections.singletonList(PortfolioItemSummary.of("1", "SWAP", "MySwap")),
          MarginSummary.of(260d, Collections.emptyList(), MarginBreakdown.of(260d, 260d, 0, 0)),
          null,
          null,
          Collections.emptyList()));
  private static final String RESPONSE_DELETE = "";
  private static final String RESPONSE_ERROR = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      ErrorMessage.of(500, "Error", "Error"));

  private MockWebServer server;

  //-------------------------------------------------------------------------
  @BeforeMethod
  public void setUp() throws IOException {
    server = new MockWebServer();
    server.start();
  }

  @AfterMethod
  public void tearDown() throws IOException {
    server.shutdown();
  }

  //-------------------------------------------------------------------------
  public void test_listCcps() {
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/json")
        .setBody(RESPONSE_LIST_CCPS));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    CcpsResult ccps = client.listCcps();
    assertEquals(ccps.getCcpNames().size(), 2);
    assertEquals(ccps.getCcpNames().get(0), Ccp.LCH.name());
    assertEquals(ccps.getCcpNames().get(1), "RUBBISH");
    assertEquals(ccps.isCcpAvailable(Ccp.LCH), true);
    assertEquals(ccps.isCcpAvailable(Ccp.CME), false);
  }

  public void test_getCcpInfo() {
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/json")
        .setBody(RESPONSE_GET_CCP_INFO));

    //call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    LocalDate expectedValuationDate = LocalDate.of(2017, 6, 1);
    String expectedCurrency = "GBP";

    CcpInfo ccpInfo = client.getCcpInfo(Ccp.LCH);
    assertEquals(ccpInfo.getCalculationCurrencies(), Collections.singletonList(expectedCurrency));
    assertEquals(ccpInfo.getReportingCurrencies(), Collections.singletonList(expectedCurrency));
    assertEquals(ccpInfo.getDefaultCurrency(), expectedCurrency);
    assertEquals(ccpInfo.getValuationDates(), Collections.singletonList(expectedValuationDate));
    assertEquals(ccpInfo.getLatestValuationDate(), expectedValuationDate);
  }

  public void test_listCcps_fail() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_ERROR));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    assertThrows(IllegalStateException.class, () -> client.listCcps());
  }

  //-------------------------------------------------------------------------
  @SuppressWarnings("deprecation")
  public void test_calculate() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(202)
        .setHeader("Location", server.url("/ccps/lch/calculations/789"))
        .setBody(RESPONSE_CALC_POST));
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_CALC_GET_PENDING));
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_CALC_GET_COMPLETE));
    server.enqueue(new MockResponse()
        .setBody(RESPONSE_DELETE));

    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    MarginCalcResult result = client.calculate(Ccp.LCH, REQUEST);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getCalculationTypes(), set(MarginCalcType.MARGIN));
    assertEquals(result.getValuationDate(), VAL_DATE);
  }

  public void test_calculate_with_retries_failing() throws Exception {
    server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
    server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
    server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

    ServiceInvoker invoker = createInvoker(1, 1);
    MarginClient client = MarginClient.of(invoker);
    assertThrows(UncheckedIOException.class, () -> client.listCcps());
    assertThrows(UncheckedIOException.class, () -> client.getCcpInfo(Ccp.LCH));
    assertThrows(UncheckedIOException.class, () -> client.calculate(Ccp.LCH, REQUEST));
  }

  @SuppressWarnings("deprecation")
  public void test_calculate_with_retries_succeeding() throws Exception {
    server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
    server.enqueue(new MockResponse()
        .setResponseCode(202)
        .setHeader("Location", server.url("/ccps/lch/calculations/789"))
        .setBody(RESPONSE_CALC_POST));
    server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_CALC_GET_PENDING));
    server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_CALC_GET_COMPLETE));
    server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
    server.enqueue(new MockResponse()
        .setBody(RESPONSE_DELETE));

    ServiceInvoker invoker = createInvoker(1, 3);
    MarginClient client = MarginClient.of(invoker);

    MarginCalcResult result = client.calculate(Ccp.LCH, REQUEST);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getCalculationTypes(), set(MarginCalcType.MARGIN));
    assertEquals(result.getValuationDate(), VAL_DATE);
  }

  // This method handles two concurrent HTTP requests, thus defines the MockWebServer in a different way.
  // For any what-if scenario request, the sequence of HTTP requests should look like this:
  // * POST - /margin/v1/ccps/lch/calculations - base portfolios
  // * POST - /margin/v1/ccps/lch/calculations - delta portfolios
  // * (for each portfolio) GET - /margin/v1/ccps/lch/calculations/[calcID] - until the status is COMPLETED.
  // * (for each portfolio) DELETE - /margin/v1/ccps/lch/calculations/[calcID]
  @SuppressWarnings("deprecation")
  public void test_calculate_whatif() throws Exception {
    Dispatcher webServerDispatcher = new Dispatcher() {
      boolean firstRequestSubmitted = false;
      boolean firstCalcRequested = false;
      boolean secondCalcRequested = false;

      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        String requestPath = request.getPath();
        if (request.getMethod().equals("POST") && requestPath.equals("/margin/v3/ccps/lch/calculations")) {
          if (!firstRequestSubmitted) {
            firstRequestSubmitted = true;
            return new MockResponse()
                .setResponseCode(202)
                .setHeader("Location", server.url("/ccps/lch/calculations/789"))
                .setBody(RESPONSE_CALC_POST);
          } else {
            return new MockResponse()
                .setResponseCode(202)
                .setHeader("Location", server.url("/ccps/lch/calculations/790"))
                .setBody(RESPONSE_CALC_POST);
          }
        } else if (request.getMethod().equals("GET") && requestPath.equals("/margin/v3/ccps/lch/calculations/789")) {
          if (!firstCalcRequested) {
            firstCalcRequested = true;
            return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(RESPONSE_CALC_GET_PENDING);
          } else {
            return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(RESPONSE_CALC_GET_COMPLETE);
          }
        } else if (request.getMethod().equals("GET") && requestPath.equals("/margin/v3/ccps/lch/calculations/790")) {
          if (!secondCalcRequested) {
            secondCalcRequested = true;
            return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(RESPONSE_CALC_GET_PENDING);
          } else {
            return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(RESPONSE_CALC_WHATIF_GET_COMPLETE);
          }
        } else if (request.getMethod().equals("DELETE")) {
          return new MockResponse()
              .setBody(RESPONSE_DELETE);
        } else {

          return new MockResponse().setResponseCode(404);
        }
      }
    };
    server.setDispatcher(webServerDispatcher);

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    PortfolioDataFile lchPortfolioFile = PortfolioDataFile.of(Paths.get(
        "src/test/resources/lch-trades.txt"));
    MarginCalcRequest.of(VAL_DATE, "GBP", Collections.singletonList(lchPortfolioFile));

    MarginWhatIfCalcResult result = client.calculateWhatIf(Ccp.LCH, REQUEST, Collections.singletonList(lchPortfolioFile)); //Using the same portfolio for delta as well
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getCalculationTypes(), set(MarginCalcType.MARGIN));
    assertEquals(result.getValuationDate(), VAL_DATE);

    assertEquals(result.getBaseSummary().getMargin(), 125.0); //Hard coded result, not relevant for portfolio
    assertEquals(result.getCombinedSummary().getMargin(), 260.0); //Hard coded result, not relevant for portfolio
    assertEquals(result.getDeltaSummary().getMargin(), 135.0);
  }

  public void test_calculate_postFail() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_ERROR));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    assertThrows(IllegalStateException.class, () -> client.calculate(Ccp.LCH, REQUEST));
  }

  public void test_calculate_getFail() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(202)
        .setHeader("Location", server.url("/ccps/lch/calculations/789"))
        .setBody(RESPONSE_CALC_POST));
    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_ERROR));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    assertThrows(IllegalStateException.class, () -> client.calculate(Ccp.LCH, REQUEST));
  }

  public void test_calculate_deleteFail() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(202)
        .setHeader("Location", server.url("/ccps/lch/calculations/789"))
        .setBody(RESPONSE_CALC_POST));
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_CALC_GET_COMPLETE));
    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_ERROR));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    // succeeds - delete failure ignored
    client.calculate(Ccp.LCH, REQUEST);
  }

  //-------------------------------------------------------------------------
  public void test_delete_fail() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_ERROR));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    assertThrows(IllegalStateException.class, () -> client.deleteCalculation(Ccp.LCH, "789"));
  }

  //-------------------------------------------------------------------------
  @SuppressWarnings("deprecation")
  public void test_calculateAsync() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(202)
        .setHeader("Location", server.url("/ccps/lch/calculations/789"))
        .setBody(RESPONSE_CALC_POST));
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_CALC_GET_PENDING));
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_CALC_GET_COMPLETE));
    server.enqueue(new MockResponse()
        .setBody(RESPONSE_DELETE));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    CompletableFuture<MarginCalcResult> future = client.calculateAsync(Ccp.LCH, REQUEST);
    MarginCalcResult result = future.join();
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getCalculationTypes(), set(MarginCalcType.MARGIN));
    assertEquals(result.getValuationDate(), VAL_DATE);
  }

  public void test_calculateAsync_createError() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(500));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    CompletableFuture<MarginCalcResult> future = client.calculateAsync(Ccp.LCH, REQUEST);
    assertThrows(CompletionException.class, () -> future.join());
    assertEquals(server.getRequestCount(), 1);
  }

  public void test_calculateAsync_pollingError() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(202)
        .setHeader("Location", server.url("/ccps/lch/calculations/789"))
        .setBody(RESPONSE_CALC_POST));
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_CALC_GET_PENDING));
    server.enqueue(new MockResponse()
        .setResponseCode(500));
    server.enqueue(new MockResponse()
        .setBody(RESPONSE_DELETE));

    // call server
    ServiceInvoker invoker = createInvoker();
    MarginClient client = MarginClient.of(invoker);

    CompletableFuture<MarginCalcResult> future = client.calculateAsync(Ccp.LCH, REQUEST);
    assertThrows(CompletionException.class, () -> future.join());
    assertEquals(server.getRequestCount(), 4);
  }

  private ServiceInvoker createInvoker() {
    return ServiceInvoker.builder(CREDENTIALS)
        .serviceUrl(server.url("/"))
        .authClientFactory(inv -> new TestingAuthClient())
        .build();
  }

  private ServiceInvoker createInvoker(int timeoutInSeconds, int retries) {
    return ServiceInvoker.builder(CREDENTIALS)
        .serviceUrl(server.url("/"))
        .authClientFactory(inv -> new TestingAuthClient())
        .httpClient(new OkHttpClient().newBuilder()
            .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .build())
        .retries(retries)
        .build();
  }

  @SafeVarargs
  private static final <T> Set<T> set(T... array) {
    return new HashSet<>(Arrays.asList(array));
  }

}
