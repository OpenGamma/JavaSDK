/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.joda.beans.ser.JodaBeanSer;

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;
import com.opengamma.sdk.margin.Ccp;
import com.opengamma.sdk.margin.CcpInfo;
import com.opengamma.sdk.margin.CcpsResult;
import com.opengamma.sdk.margin.MarginCalcRequest;
import com.opengamma.sdk.margin.MarginCalcResult;
import com.opengamma.sdk.margin.MarginClient;
import com.opengamma.sdk.margin.PortfolioDataFile;

/**
 * Example code for the OpenGamma Margin Service API (v3)
 */
public class MarginClientExample {

  // credentials must be set to use the example
  private static final String DEV_ID = "PLEASE-CONTACT-OPENGAMMA";
  private static final String DEV_SECRET = "123";
  private static final Credentials CREDENTIALS = Credentials.ofApiKey(DEV_ID, DEV_SECRET);

  // the file to upload
  private static final Path LCH_FILE = Paths.get("examples/src/main/resources/com/opengamma/sdk/example/lch-trades.txt");

  // example code - invoke with no arguments
  public static void main(String[] args) throws InterruptedException {
    // create the invoker specifying the URL and credentials
    try (ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS)) {
      // Creating the margin client
      MarginClient client = MarginClient.of(invoker);

      // Listing the CCPs that are available
      CcpsResult ccps = client.listCcps();

      // Optional step: Checking if we are permissioned to the specific CCP calculation engine
      Ccp chosenCCP = Ccp.LCH;
      if (!ccps.isCcpAvailable(chosenCCP)) {
        throw new IllegalStateException("Margin Calculator not available for " + chosenCCP.name());
      }

      //Retrieve specific information about the CCP calculation engine: valuation dates and available currencies
      CcpInfo lch = client.getCcpInfo(chosenCCP);
      LocalDate valuationDate = lch.getLatestValuationDate();
      LocalDate older = LocalDate.of(2021, 7, 27);
      ExecutorService executor = Executors.newFixedThreadPool(30);
      try {
        CompletableFuture[] array = IntStream.range(0, 100)
            .mapToObj(i -> {

              return Stream.of("EUR", "USD", "GBP")
                  .map(currency -> CompletableFuture.supplyAsync(
                      () -> {

                        //String currency = lch.getDefaultCurrency();

                        // choose the file to upload
                        List<PortfolioDataFile> files = Collections.singletonList(PortfolioDataFile.of(LCH_FILE));

                        // create the request
                        MarginCalcRequest request = MarginCalcRequest.of(older, currency, files);

                        // make the call and view the result
                        MarginCalcResult result = client.calculate(chosenCCP, request);
                        System.out.println(JodaBeanSer.PRETTY.simpleJsonWriter().write(result));
                        return result;
                      },
                      executor));
            })
            .flatMap(f -> f)
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(array).join();
      } finally {
        executor.shutdown();
      }

      // make the what-if call and view the result (the difference in margin numbers)
      //MarginWhatIfCalcResult whatIfResult =
      //    client.calculateWhatIf(Ccp.LCH, request, Collections.singletonList(PortfolioDataFile.of(LCH_FILE)));
      //System.out.println(JodaBeanSer.PRETTY.simpleJsonWriter().write(whatIfResult));
    }
  }

}
