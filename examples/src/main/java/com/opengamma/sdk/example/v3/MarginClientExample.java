/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.example.v3;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.joda.beans.ser.JodaBeanSer;

import com.opengamma.sdk.common.auth.v3.Credentials;
import com.opengamma.sdk.common.v3.ServiceInvoker;
import com.opengamma.sdk.margin.v3.MarginWhatIfCalcResult;
import com.opengamma.sdk.margin.v3.Ccp;
import com.opengamma.sdk.margin.v3.CcpInfo;
import com.opengamma.sdk.margin.v3.CcpsResult;
import com.opengamma.sdk.margin.v3.MarginCalcRequest;
import com.opengamma.sdk.margin.v3.MarginCalcResult;
import com.opengamma.sdk.margin.v3.MarginClient;
import com.opengamma.sdk.margin.v3.PortfolioDataFile;

/**
 * Example code for the OpenGamma Margin Service API (v3)
 */
public class MarginClientExample {

  // credentials must be set to use the example
  private static final Credentials CREDENTIALS = Credentials.ofApiKey("ABC", "123");

  // the file to upload
  private static final Path LCH_FILE = Paths.get("src/main/resources/lch-trades.txt");


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
      if(!ccps.isCcpAvailable(chosenCCP)) {
        throw new IllegalStateException(chosenCCP.name() + "Margin Calculator not available");
      }

      //Retrieve specific information about the CCP calculation engine: valuation dates and available currencies
      CcpInfo lch = client.getCcpInfo(chosenCCP);
      LocalDate valuationDate = lch.getLatestValuationDate();
      String currency = lch.getDefaultCurrency();

      // choose the file to upload
      List<PortfolioDataFile> files = Collections.singletonList(PortfolioDataFile.of(LCH_FILE));

      // create the request
      MarginCalcRequest request = MarginCalcRequest.of(valuationDate, currency, files);

      // make the call and view the result
      MarginCalcResult result = client.calculate(chosenCCP, request);
      System.out.println(JodaBeanSer.PRETTY.jsonWriter().write(result));

      // make the what-if call and view the result (the difference in margin numbers)
      MarginWhatIfCalcResult whatIfResult = client.calculateWhatIf(Ccp.LCH, request, Collections.singletonList(PortfolioDataFile.of(LCH_FILE)));
      System.out.println(JodaBeanSer.PRETTY.jsonWriter().write(whatIfResult));
    }
  }

}
