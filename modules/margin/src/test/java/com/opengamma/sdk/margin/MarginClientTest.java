/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.joda.beans.ser.JodaBeanSer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Test.
 */
@Test
@SuppressWarnings("deprecation")
public class MarginClientTest {

  private static final Credentials CREDENTIALS = Credentials.ofUsernamePassword("user", "password");
  private static final LocalDate VAL_DATE = LocalDate.of(2017, 6, 1);
  private static final MarginCalcRequest REQUEST =
      MarginCalcRequest.of(VAL_DATE, "GBP", Collections.emptyList(), MarginCalcRequestType.STANDARD, false);

  private static final String RESPONSE_LIST_CCPS = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      CcpsResult.of(Collections.singletonList(
          CcpInfo.of(
              Ccp.LCH,
              URI.create("/ccps/lch"),
              Collections.singletonList(VAL_DATE),
              "GBP",
              Collections.singletonList("GBP"),
              Collections.singletonList("GBP")))));
  private static final String RESPONSE_CALC_POST = "";
  private static final String RESPONSE_CALC_GET_PENDING = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      MarginCalcResult.of(
          MarginCalcResultStatus.PENDING,
          MarginCalcRequestType.STANDARD,
          VAL_DATE,
          "GBP",
          Collections.emptyList(),
          null,
          Collections.emptyList()));
  private static final String RESPONSE_CALC_GET_COMPLETE = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      MarginCalcResult.of(
          MarginCalcResultStatus.COMPLETED,
          MarginCalcRequestType.STANDARD,
          VAL_DATE,
          "GBP",
          Collections.singletonList(PortfolioItemSummary.of("1", "SWAP", "MySwap")),
          MarginSummary.of(125d, Collections.emptyList()),
          Collections.emptyList()));
  private static final String RESPONSE_CALC_WHATIF_GET_COMPLETE = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      MarginCalcResult.of(
          MarginCalcResultStatus.COMPLETED,
          MarginCalcRequestType.STANDARD,
          VAL_DATE,
          "GBP",
          Collections.singletonList(PortfolioItemSummary.of("1", "SWAP", "MySwap")),
          MarginSummary.of(260d, Collections.emptyList()),
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
  public void test_listCcps() throws Exception {
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_LIST_CCPS));

    // call server
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
    MarginClient client = MarginClient.of(invoker);

    CcpsResult ccps = client.listCcps();
    assertEquals(ccps.getCcps().size(), 1);
    assertEquals(ccps.getCcps().get(0).getName(), Ccp.LCH);
    assertEquals(ccps.findCcp(Ccp.LCH).get().getName(), Ccp.LCH);
    assertEquals(ccps.getCcp(Ccp.LCH).getName(), Ccp.LCH);
    assertEquals(ccps.getCcp(Ccp.LCH).getLatestValuationDate(), VAL_DATE);
  }

  public void test_listCcps_fail() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_ERROR));

    // call server
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
    MarginClient client = MarginClient.of(invoker);

    assertThrows(IllegalStateException.class, () -> client.listCcps());
  }

  //-------------------------------------------------------------------------
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

    // call server
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
    MarginClient client = MarginClient.of(invoker);

    MarginCalcResult result = client.calculate(Ccp.LCH, REQUEST);
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getValuationDate(), VAL_DATE);
  }

  /**
   * This method handles two concurrent HTTP requests, and has to define the {@link MockWebServer} instance in a different way.
   * For any what-if scenario request, the sequence of HTTP requests should look like this:
   * * POST - /margin/v1/ccps/lch/calculations - base portfolios
   * * POST - /margin/v1/ccps/lch/calculations - delta portfolios
   * * (for each portfolio) GET - /margin/v1/ccps/lch/calculations/[calcID] - until the status is COMPLETED.
   * * (for each portfolio) DELETE - /margin/v1/ccps/lch/calculations/[calcID]
   */
  public void test_calculate_whatif() throws Exception {
    Dispatcher webServerDispatcher = new Dispatcher() {
      boolean firstRequestSubmitted = false;
      boolean firstCalcRequested = false;
      boolean secondCalcRequested = false;

      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        String requestPath = request.getPath();
        if (request.getMethod().equals("POST") && requestPath.equals("/margin/v1/ccps/lch/calculations")) {
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
        } else if (request.getMethod().equals("GET") && requestPath.equals("/margin/v1/ccps/lch/calculations/789")) {
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
        } else if (request.getMethod().equals("GET") && requestPath.equals("/margin/v1/ccps/lch/calculations/790")) {
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
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
    MarginClient client = MarginClient.of(invoker);

    PortfolioDataFile lchPortfolioFile = PortfolioDataFile.of(Paths.get(
        "src/test/resources/lch-trades.txt"));
    MarginCalcRequest.of(VAL_DATE, "GBP", Collections.singletonList(lchPortfolioFile));

    MarginWhatIfCalcResult result = client.calculateWhatIf(Ccp.LCH, REQUEST, Collections.singletonList(lchPortfolioFile)); //Using the same portfolio for delta as well
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getValuationDate(), VAL_DATE);

    assertEquals(result.getBaseSummary().getMargin(), 125.0); //Hard coded result, not relevant for portfolio
    assertEquals(result.getCombinedSummary().getMargin(), 135.0); //Hard coded result, not relevant for portfolio
    assertEquals(result.getDeltaSummary().getMargin(), 260.0);
  }

  public void test_calculate_postFail() throws Exception {
    server.enqueue(new MockResponse()
        .setResponseCode(500)
        .setHeader("Content-Type", "application/xml")
        .setBody(RESPONSE_ERROR));

    // call server
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
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
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
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
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
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
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
    MarginClient client = MarginClient.of(invoker);

    assertThrows(IllegalStateException.class, () -> client.deleteCalculation(Ccp.LCH, "789"));
  }

  //-------------------------------------------------------------------------
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
    ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS, server.url("/"), new TestingAuthClient());
    MarginClient client = MarginClient.of(invoker);

    CompletableFuture<MarginCalcResult> future = client.calculateAsync(Ccp.LCH, REQUEST);
    MarginCalcResult result = future.join();
    assertEquals(result.getStatus(), MarginCalcResultStatus.COMPLETED);
    assertEquals(result.getType(), MarginCalcRequestType.STANDARD);
    assertEquals(result.getValuationDate(), VAL_DATE);
  }

}
