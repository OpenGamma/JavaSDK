/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.beans.ser.JodaBeanSer;

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
 * Example code for the OpenGamma Margin Service API (v3)
 */
public class MarginClientExample {

  // credentials must be set to use the example
  private static final String DEV_ID = "PLEASE-CONTACT-OPENGAMMA";
  private static final String DEV_SECRET = "123";
  private static final Credentials CREDENTIALS = Credentials.ofApiKey(DEV_ID, DEV_SECRET);
  private static final Map<Ccp, Path> CCP_FILES;

  static {
    Map<Ccp, Path> ccpMap = new HashMap<>();
    ccpMap.put(Ccp.LCH, Paths.get("src/main/resources/com/opengamma/sdk/example/lch-trades.txt"));
    ccpMap.put(Ccp.CME, Paths.get("src/main/resources/com/opengamma/sdk/example/cme.csv"));
    ccpMap.put(Ccp.ICE_SPAN, Paths.get("src/main/resources/com/opengamma/sdk/example/ice_span.csv"));
    CCP_FILES = Collections.unmodifiableMap(ccpMap);
  }

  /**
   * Invoke without any arguments to calculate for LCH.
   * <p>
   * Provide CCPs as arguments to run calculations for those CCPs. Valid CCPs:
   * <ul>
   *   <li>LCH</li>
   *   <li>CME</li>
   *   <li>ICE_SPAN</li>
   * </ul>
   *
   * @param args empty to calculate for LCH, otherwise list of CCPs for which to run calculations.
   */
  public static void main(String[] args) {
    List<Ccp> ccps = new ArrayList<>();

    if (args.length == 0) {
      ccps.add(Ccp.LCH);
    } else {
      for (String arg : args) {
        Ccp ccp = Ccp.of(arg);
        ccps.add(ccp);
      }
    }

    // create the invoker specifying the URL and credentials
    try (ServiceInvoker invoker = ServiceInvoker.of(CREDENTIALS)) {
      // Creating the margin client
      MarginClient client = MarginClient.of(invoker);

      // Listing the CCPs that are available
      CcpsResult ccpsResult = client.listCcps();

      for (Ccp ccp : ccps) {
        if (!CCP_FILES.containsKey(ccp)) {
          throw new IllegalStateException("Example portfolio data not available for " + ccp.name());
        }
        if (!ccpsResult.isCcpAvailable(ccp)) {
          throw new IllegalStateException("Margin Calculator not available for " + ccp.name());
        }
        calculate(client, ccp);
      }
    }
  }

  private static void calculate(MarginClient client, Ccp ccp) {
    System.out.println("Calculating for " + ccp.name());
    //Retrieve specific information about the CCP calculation engine: valuation dates and available currencies
    CcpInfo ccpInfo = client.getCcpInfo(ccp);
    LocalDate valuationDate = ccpInfo.getLatestValuationDate();
    String currency = ccpInfo.getDefaultCurrency();

    // choose the file to upload
    Path portfolioFile = CCP_FILES.get(ccp);
    List<PortfolioDataFile> files = Collections.singletonList(PortfolioDataFile.of(portfolioFile));

    // create the request
    MarginCalcRequest request = MarginCalcRequest.of(valuationDate, currency, files);

    // make the call and view the result
    MarginCalcResult result = client.calculate(ccp, request);
    System.out.println("Results for " + ccp.name());
    System.out.println(JodaBeanSer.PRETTY.simpleJsonWriter().write(result));

    // make the what-if call and view the result (the difference in margin numbers)
    System.out.println("Calculating what-if for " + ccp.name());
    MarginWhatIfCalcResult whatIfResult =
        client.calculateWhatIf(ccp, request, Collections.singletonList(PortfolioDataFile.of(portfolioFile)));
    System.out.println("What-if results for " + ccp.name());
    System.out.println(JodaBeanSer.PRETTY.simpleJsonWriter().write(whatIfResult));
  }
}
