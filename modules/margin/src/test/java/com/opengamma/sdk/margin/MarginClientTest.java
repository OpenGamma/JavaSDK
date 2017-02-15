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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.joda.beans.ser.JodaBeanSer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Test.
 */
@Test
public class MarginClientTest {

  private static final Credentials CREDENTIALS = Credentials.ofUsernamePassword("user", "password");
  private static final LocalDate VAL_DATE = LocalDate.of(2017, 6, 1);
  private static final MarginCalcRequest REQUEST =
      MarginCalcRequest.of(VAL_DATE, "GBP", Arrays.asList(), MarginCalcRequestType.STANDARD, false);

  private static final String RESPONSE_LIST_CCPS = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      CcpsResult.of(Arrays.asList(
          CcpInfo.of(
              Ccp.LCH,
              URI.create("/ccps/lch"),
              Arrays.asList(VAL_DATE),
              "GBP",
              Arrays.asList("GBP"),
              Arrays.asList("GBP")))));
  private static final String RESPONSE_CALC_POST = "";
  private static final String RESPONSE_CALC_GET_PENDING = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      MarginCalcResult.of(
          MarginCalcResultStatus.PENDING,
          MarginCalcRequestType.STANDARD,
          VAL_DATE,
          "GBP",
          Arrays.asList(),
          null,
          Arrays.asList()));
  private static final String RESPONSE_CALC_GET_COMPLETE = JodaBeanSer.PRETTY.simpleJsonWriter().write(
      MarginCalcResult.of(
          MarginCalcResultStatus.COMPLETED,
          MarginCalcRequestType.STANDARD,
          VAL_DATE,
          "GBP",
          Arrays.asList(PortfolioItemSummary.of("1", "SWAP", "MySwap")),
          MarginSummary.of(125d, Arrays.asList()),
          Arrays.asList()));
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
