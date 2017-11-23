/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.v3;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.opengamma.sdk.common.v3.ServiceInvoker;

/**
 * Client providing access to the margin service.
 */
public interface MarginClient {

  /**
   * Obtains an instance, specifying the invoker to use.
   * <p>
   * The {@link ServiceInvoker} provides authentication.
   *
   * @param invoker  the service invoker
   * @return the client
   */
  public static MarginClient of(ServiceInvoker invoker) {
    return InvokerMarginClient.of(invoker);
  }

  //-------------------------------------------------------------------------
  /**
   * Lists the available CCPs.
   *
   * @return the list of available CCPs
   * @throws MarginException if unable to list the CCPS
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract CcpsResult listCcps();

  /**
   * Gets information about a single CCP.
   *
   * @param ccp  the CCP to lookup
   * @return the information about the CCP
   * @throws MarginException if unable to get the information
   * @throws UncheckedIOException if an IO error occurs
   */
  public CcpInfo getCcpInfo(Ccp ccp);

  /**
   * Creates a margin calculation task.
   *
   * @param ccp  the CCP to use
   * @param request  the calculation request
   * @return the calculation identifier
   * @throws MarginException if unable to create the calculation
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract String createCalculation(Ccp ccp, MarginCalcRequest request);

  /**
   * Gets the result of a margin calculation task.
   *
   * @param ccp  the CCP to use
   * @param calcId  the calculation identifier
   * @return the calculation result
   * @throws MarginException if unable to get the calculation
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract MarginCalcResult getCalculation(Ccp ccp, String calcId);

  /**
   * Deletes a margin calculation task.
   *
   * @param ccp  the CCP to use
   * @param calcId  the calculation identifier
   * @throws MarginException if unable to delete the calculation
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract void deleteCalculation(Ccp ccp, String calcId);

  //-------------------------------------------------------------------------
  /**
   * High-level call to submit a portfolio for parsing, validation and IM calculation.
   *
   * @param ccp  the CCP to use
   * @param request  the calculation request
   * @return the detailed result of the calculation
   * @throws MarginException if unable to calculate
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract MarginCalcResult calculate(Ccp ccp, MarginCalcRequest request);

  /**
   * High-level call to submit a portfolio for parsing, validation and IM calculation,
   * performing the work on a background thread.
   * <p>
   * This will use the executor from the service invoker to perform the background work.
   *
   * @param ccp  the CCP to use
   * @param request  the calculation request
   * @return the detailed result of the calculation, expressed via a future
   * @throws RuntimeException if unable to setup the async calculation
   */
  public abstract CompletableFuture<MarginCalcResult> calculateAsync(Ccp ccp, MarginCalcRequest request);

  //-------------------------------------------------------------------------
  /**
   * High-level call to submit a base portfolio together with an extra set of trades,
   * for parsing, validation and IM calculation.
   * <p>
   * This will return the margin summary for the base request, the combined request (base portfolio + delta portfolio),
   * and the difference between the two.
   *
   * @param ccp  the CCP to use
   * @param request  the calculation request
   * @param deltaFiles  the portfolios representing the extra trades for the what-if scenario
   * @return the detailed result of the calculation
   * @throws MarginException if unable to calculate
   * @throws UncheckedIOException if an IO error occurs
   */
  public abstract MarginWhatIfCalcResult calculateWhatIf(
      Ccp ccp,
      MarginCalcRequest request,
      List<PortfolioDataFile> deltaFiles);

}
