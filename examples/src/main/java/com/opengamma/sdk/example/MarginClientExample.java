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

import com.opengamma.sdk.common.ServiceInvoker;
import com.opengamma.sdk.common.auth.Credentials;
import com.opengamma.sdk.margin.Ccp;
import com.opengamma.sdk.margin.CcpInfo;
import com.opengamma.sdk.margin.CcpsResult;
import com.opengamma.sdk.margin.MarginCalcRequest;
import com.opengamma.sdk.margin.MarginCalcResult;
import com.opengamma.sdk.margin.MarginClient;
import com.opengamma.sdk.margin.MarginWhatIfCalcResult;
import com.opengamma.sdk.margin.PortfolioDataFile;

/**
 * Example code.
 */
public class MarginClientExample {

  // credentials must be set to use the example
  private static final Credentials CREDENTIALS = Credentials.ofApiKey("ABC", "123");

  // the file to upload
  private static final Path LCH_FILE = Paths.get("src/main/resources/lch-trades.txt");

  // example code - invoke with no arguments
  public static void main(String[] args) {
    // create the invoker specifying the URL and credentials
    try (ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS)) {
      // create the margin client
      MarginClient client = MarginClient.of(invoker);

      // list the available CCPs, valuation dates and currencies
      CcpsResult ccps = client.listCcps();

      // extract the information about the CCP to call
      CcpInfo lch = ccps.getCcp(Ccp.LCH);
      LocalDate valuationDate = lch.getLatestValuationDate();
      String currency = lch.getDefaultCurrency();

      // choose the file to upload
      List<PortfolioDataFile> files = Collections.singletonList(PortfolioDataFile.of(LCH_FILE));

      // create the request
      MarginCalcRequest request = MarginCalcRequest.of(valuationDate, currency, files);

      // make the call and view the result
      MarginCalcResult result = client.calculate(Ccp.LCH, request);
      System.out.println(result);

      // make the what-if call and view the result (the difference in margin numbers)
      MarginWhatIfCalcResult whatIfResult = client.calculateWhatIf(Ccp.LCH, request, Collections.singletonList(PortfolioDataFile.of(LCH_FILE)));
      System.out.println(whatIfResult);
    }

  }

}
