/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;
import com.opengamma.sdk.margin.Ccp;
import com.opengamma.sdk.margin.CcpInfo;
import com.opengamma.sdk.margin.MarginCalcMode;
import com.opengamma.sdk.margin.MarginCalcRequest;
import com.opengamma.sdk.margin.MarginCalcRequestType;
import com.opengamma.sdk.margin.MarginCalcResult;
import com.opengamma.sdk.margin.MarginClient;
import com.opengamma.sdk.margin.PortfolioDataFile;

import okhttp3.HttpUrl;

public class StressTest {

  private static final Path LCH_FILE = Paths.get("src/main/resources/com/opengamma/sdk/example/lch-trades.txt");

  public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {

  }

  private static void runTest(Credentials credentials, String url) throws InterruptedException {
    Ccp chosenCCP = Ccp.LCH;

    HttpUrl httpUrl = HttpUrl.parse(url);
    try (ServiceInvoker invoker = ServiceInvoker.of(credentials, httpUrl)) {
      MarginClient client = MarginClient.of(invoker);
      CcpInfo ccpInfo = client.getCcpInfo(chosenCCP);

      List<PortfolioDataFile> fileToRun = Collections.singletonList(PortfolioDataFile.of(LCH_FILE));
      MarginCalcRequest request = MarginCalcRequest.of(
          ccpInfo.getLatestValuationDate(),
          "EUR",
          MarginCalcMode.SPOT,
          fileToRun,
          MarginCalcRequestType.STANDARD,
          true);

      Queue<CompletableFuture<MarginCalcResult>> list = new ConcurrentLinkedQueue<>();
      new ForkJoinPool(1000).submit(() -> {
        IntStream.range(0, 1_000_000).parallel().forEach((id) -> {
          CompletableFuture<MarginCalcResult> future = client.calculateAsync(chosenCCP, request);
          list.add(future);
        });
      });
      Thread.sleep(1_000_000);
    }
  }

  private static Credentials getDevCredentials() {
    return Credentials.ofApiKey(
        "aFDE2uFZKLllo90IyAJaeqoHTTRNvIRS",
        System.getenv("DEV_CREDENTIALS"));
  }

  private static Credentials getProdCredentials() {
    return Credentials.ofApiKey(
        "1NyWPB1qnTAhl3JxFw8I9QXCMeaxlmzl",
        System.getenv("PROD_CREDENTIALS"));
  }
}
