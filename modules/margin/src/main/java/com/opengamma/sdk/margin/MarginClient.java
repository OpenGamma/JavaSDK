/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.opengamma.sdk.common.ServiceInvoker;

/**
 * Client providing access to the margin service.
 *
 * @deprecated Since 1.3.0. Replaed by {@link com.opengamma.sdk.margin.v3.MarginClient} with updated signatures.
 *   The current interface will be removed in future versions.
 */
@Deprecated
public interface MarginClient {

  /**
   * Obtains an instance.
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
   */
  public abstract CcpsResult listCcps();

  /**
   * Creates a margin calculation task.
   * 
   * @param ccp  the CCP to use
   * @param request  the calculation request
   * @return the calculation identifier
   */
  public abstract String createCalculation(Ccp ccp, MarginCalcRequest request);

  /**
   * Gets the result of a margin calculation task.
   *
   * @param ccp  the CCP to use
   * @param calcId  the calculation identifier
   * @return the calculation result
   * @throws IllegalArgumentException if the calculation is not found
   */
  public abstract MarginCalcResult getCalculation(Ccp ccp, String calcId);

  /**
   * Deletes a margin calculation task.
   *
   * @param ccp  the CCP to use
   * @param calcId  the calculation identifier
   * @throws IllegalArgumentException if the calculation is not found
   */
  public abstract void deleteCalculation(Ccp ccp, String calcId);

  //-------------------------------------------------------------------------
  /**
   * High-level call to submit a portfolio for parsing, validation and IM calculation.
   *
   * @param ccp  the CCP to use
   * @param request  the calculation request
   * @return the detailed result of the calculation
   */
  public abstract MarginCalcResult calculate(Ccp ccp, MarginCalcRequest request);

  /**
   * High-level call to submit a portfolio for parsing, validation and IM calculation.
   *
   * @param ccp  the CCP to use
   * @param request  the calculation request
   * @return the detailed result of the calculation, expressed via a future
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
   */
  public abstract MarginWhatIfCalcResult calculateWhatIf(Ccp ccp, MarginCalcRequest request, List<PortfolioDataFile> deltaFiles);

}
