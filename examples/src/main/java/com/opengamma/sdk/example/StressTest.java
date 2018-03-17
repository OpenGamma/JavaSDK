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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;
import com.opengamma.sdk.margin.Ccp;
import com.opengamma.sdk.margin.CcpInfo;
import com.opengamma.sdk.margin.MarginCalcMode;
import com.opengamma.sdk.margin.MarginCalcRequest;
import com.opengamma.sdk.margin.MarginCalcRequestType;
import com.opengamma.sdk.margin.MarginCalcResult;
import com.opengamma.sdk.margin.MarginCalcResultStatus;
import com.opengamma.sdk.margin.MarginClient;
import com.opengamma.sdk.margin.MarginError;
import com.opengamma.sdk.margin.PortfolioDataFile;
import com.sun.org.apache.xerces.internal.xs.StringList;

import okhttp3.HttpUrl;

public class StressTest {

  private static final Path LCH_FILE = Paths.get("/Users/krzysztofhuszcza/Checkout/JavaSDK/examples/src/main/resources/com/opengamma/sdk/example/lch-trades.txt");

  public static void main(String[] args) throws InterruptedException, IOException, ExecutionException, TimeoutException {
    runTest(getDevCredentials(), "https://api.dev.opengamma.com");
  }

  private static void runTest(Credentials credentials, String url) throws InterruptedException, TimeoutException, ExecutionException {
    Ccp chosenCCP = Ccp.LCH;

    HttpUrl httpUrl = HttpUrl.parse(url);
    try (ServiceInvoker invoker = ServiceInvoker.of(credentials, httpUrl)) {
      MarginClient client = MarginClient.of(invoker);
      CcpInfo ccpInfo = client.getCcpInfo(chosenCCP);

      List<PortfolioDataFile> fileToRun = Collections.singletonList(PortfolioDataFile.of(LCH_FILE));
      MarginCalcRequest request = MarginCalcRequest.of(
          ccpInfo.getLatestValuationDate(),
          "GBP",
          MarginCalcMode.SPOT,
          fileToRun,
          MarginCalcRequestType.STANDARD,
          true);

      Queue<String> ids = new ConcurrentLinkedQueue<>();
      Queue<String> submissionErrors = new ConcurrentLinkedQueue<>();

      new ForkJoinPool(1000).submit(() -> {
        IntStream.range(0, 100).parallel().forEach((id) -> {
          try {
            for (int j = 0; j < 100; j++) {
              try {
                String calcId = client.createCalculation(chosenCCP, request);
                ids.add(calcId);
              } catch (Exception e) {
                submissionErrors.add(e.getMessage());
                System.out.println("Submitter backing off 1 sec, because of: " + e.getMessage());
                Thread.sleep(1000);
              }

              if (j != 0 && j % 5 == 0) {
                System.out.println("Submitted " + j + " requests, sleeping 1 seconds");
                Thread.sleep(1000);
              }
            }
          } catch (Exception e) {
            submissionErrors.add("COMPLETE SUBMITTER FAILURE: " + e.getMessage());
          }
        });
      }).join();

      System.out.println("Waiting for calcs to finish");
      Thread.sleep(1000 * 60 * 5);

      System.out.println("Checking calcs");
      final AtomicInteger successful = new AtomicInteger();
      final AtomicInteger failed = new AtomicInteger();
      final AtomicInteger uncompleted = new AtomicInteger();
      final Queue<String> failures = new ConcurrentLinkedQueue<>();

      new ForkJoinPool(100).submit(() -> {
        ids.stream().parallel().forEach(id -> {
          MarginCalcResult marginCalcResult;
          try {
            marginCalcResult = client.getCalculation(chosenCCP, id);
          } catch (Exception e) {
            uncompleted.incrementAndGet();
            return;
          }

          if (marginCalcResult == null) {
            uncompleted.incrementAndGet();
            return;
          }

          MarginCalcResultStatus status = marginCalcResult.getStatus();
          if (status.equals(MarginCalcResultStatus.PENDING)) {
            uncompleted.incrementAndGet();
          }
          else if (!marginCalcResult.getFailures().isEmpty()) {
            failed.incrementAndGet();
            for (MarginError marginError : marginCalcResult.getFailures()) {
              failures.add(marginError.getMessage());
            }
          }
          else {
            successful.incrementAndGet();
          }
        });
      }).join();

      System.out.println("SUBMISSION ERRORS");
      submissionErrors.forEach(System.out::println);

      System.out.println("CALC FAILURES");
      failures.forEach(System.out::println);

      System.out.println("Submitted: " + ids.size());
      System.out.println("Failed: " + failed.intValue());
      System.out.println("Uncompleted: " + uncompleted.intValue());
      System.out.println("Successful: " + successful.intValue());
      System.out.println("Submission errors: " + submissionErrors.size());
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
