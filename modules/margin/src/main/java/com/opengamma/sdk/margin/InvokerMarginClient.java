/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static com.opengamma.sdk.common.ServiceInvoker.MEDIA_JSON;
import static com.opengamma.sdk.margin.MarginOperation.CREATE_CALCULATION;
import static com.opengamma.sdk.margin.MarginOperation.DELETE_CALCULATION;
import static com.opengamma.sdk.margin.MarginOperation.GET_CALCULATION;
import static com.opengamma.sdk.margin.MarginOperation.GET_CCP_INFO;
import static com.opengamma.sdk.margin.MarginOperation.LIST_CCPS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.joda.beans.ser.JodaBeanSer;
import org.joda.beans.ser.SerDeserializers;

import com.opengamma.sdk.common.ServiceInvoker;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of the margin client.
 */
final class InvokerMarginClient implements MarginClient {

  /**
   * The serializer.
   */
  private static final JodaBeanSer SERIALIZER = JodaBeanSer.COMPACT.withDeserializers(SerDeserializers.LENIENT);
  static {
    SERIALIZER.getConverter().register(Period.class, new TenorStringConverter());
  }
  /**
   * Sleep for 500ms between polls.
   */
  private static final long POLL_WAIT = 500;
  /**
   * HTTP header.
   */
  private static final String LOCATION = "Location";

  /**
   * The service invoker.
   */
  private final ServiceInvoker invoker;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   *
   * @param invoker  the service invoker
   * @return the client
   */
  static InvokerMarginClient of(ServiceInvoker invoker) {
    return new InvokerMarginClient(invoker);
  }

  private InvokerMarginClient(ServiceInvoker invoker) {
    this.invoker = Objects.requireNonNull(invoker, "invoker must not be null");
  }

  //-------------------------------------------------------------------------
  @Override
  public CcpsResult listCcps() {
    Request request = new Request.Builder()
        .url(invoker.getServiceUrl().resolve("margin/v3/ccps"))
        .get()
        .header("Accept", MEDIA_JSON.toString())
        .build();

    try (Response response = invoker.getHttpClient().newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw parseError(LIST_CCPS, response);
      }
      return SERIALIZER.jsonReader().read(response.body().string(), CcpsResult.class);

    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public CcpInfo getCcpInfo(Ccp ccp) {
    Request request = new Request.Builder()
        .url(invoker.getServiceUrl().resolve("margin/v3/ccps/" + ccp.name().toLowerCase(Locale.ENGLISH)))
        .get()
        .header("Accept", MEDIA_JSON.toString())
        .build();

    try (Response response = invoker.getHttpClient().newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw parseError(GET_CCP_INFO, response);
      }
      return SERIALIZER.jsonReader().read(response.body().string(), CcpInfo.class);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public String createCalculation(Ccp ccp, MarginCalcRequest calcRequest) {
    String text = SERIALIZER.jsonWriter().write(calcRequest, false);
    RequestBody body = RequestBody.create(MEDIA_JSON, text);
    Request request = new Request.Builder()
        .url(invoker.getServiceUrl().resolve("margin/v3/ccps/" + ccp.name().toLowerCase(Locale.ENGLISH) + "/calculations"))
        .post(body)
        .header("Content-Type", MEDIA_JSON.toString())
        .header("Accept", MEDIA_JSON.toString())
        .build();

    try (Response response = invoker.getHttpClient().newCall(request).execute()) {
      if (response.code() != 202) {
        throw parseError(CREATE_CALCULATION, response);
      }
      String location = response.header(LOCATION);
      return location.substring(location.lastIndexOf('/') + 1);

    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public MarginCalcResult getCalculation(Ccp ccp, String calcId) {
    Request request = new Request.Builder()
        .url(invoker.getServiceUrl()
            .resolve("margin/v3/ccps/" + ccp.name().toLowerCase(Locale.ENGLISH) + "/calculations/" + calcId))
        .get()
        .header("Accept", MEDIA_JSON.toString())
        .build();

    try (Response response = invoker.getHttpClient().newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw parseError(GET_CALCULATION, response);
      }
      SerDeserializers deser = MarginDetailDeserializer.of(ccp)
          .map(ds -> new SerDeserializers(true, ds))
          .orElse(SerDeserializers.LENIENT);
      return SERIALIZER.withDeserializers(deser).jsonReader().read(response.body().string(), MarginCalcResult.class);

    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void deleteCalculation(Ccp ccp, String calcId) {
    Request request = new Request.Builder()
        .url(invoker.getServiceUrl()
            .resolve("margin/v3/ccps/" + ccp.name().toLowerCase(Locale.ENGLISH) + "/calculations/" + calcId))
        .delete()
        .header("Accept", MEDIA_JSON.toString())
        .build();

    try (Response response = invoker.getHttpClient().newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw parseError(DELETE_CALCULATION, response);
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  // throw exception in case of error
  private MarginException parseError(MarginOperation operation, Response response) throws IOException {
    ErrorMessage errorMessage = parseError(response);
    String combinedMsg = "Request '" + operation.getDescription() + "' failed. Reason: " + errorMessage.getReason() +
        ", status code: " + response.code() + ", message: " + errorMessage.getMessage();
    return new MarginException(combinedMsg, response.code(), errorMessage.getReason(), errorMessage.getMessage(), operation);
  }

  // avoid errors when processing errors
  private ErrorMessage parseError(Response response) throws IOException {
    try {
      return SERIALIZER.jsonReader().read(response.body().string(), ErrorMessage.class);
    } catch (RuntimeException ex) {
      return ErrorMessage.of(response.code(), "Unexpected JSON error", ex.getMessage());
    }
  }

  //-------------------------------------------------------------------------
  @Override
  public MarginCalcResult calculate(Ccp ccp, MarginCalcRequest request) {
    String calcId = createCalculation(ccp, request);
    MarginCalcResult result = getCalculation(ccp, calcId);
    while (result.getStatus() == MarginCalcResultStatus.PENDING) {
      try {
        Thread.sleep(POLL_WAIT);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(ex);
      }
      result = getCalculation(ccp, calcId);
    }
    // cleanup server state quietly
    try {
      deleteCalculation(ccp, calcId);
    } catch (RuntimeException ex) {
      // ignore
    }
    return result;
  }

  @Override
  public CompletableFuture<MarginCalcResult> calculateAsync(Ccp ccp, MarginCalcRequest request) {
    ScheduledExecutorService executorService = invoker.getExecutor();
    // async function to create the calculation
    Supplier<String> createFn = () -> createCalculation(ccp, request);
    // async function to poll for results
    Function<String, CompletableFuture<MarginCalcResult>> pollingFn = id -> {
      // manually manage the result future and polling
      CompletableFuture<MarginCalcResult> resultFuture = new CompletableFuture<>();
      // polling task must catch exceptions, otherwise it will poll forever
      Runnable pollTask = () -> {
        try {
          MarginCalcResult calcResult = getCalculation(ccp, id);
          if (calcResult.getStatus() == MarginCalcResultStatus.COMPLETED) {
            resultFuture.complete(calcResult);
          }
        } catch (RuntimeException ex) {
          resultFuture.completeExceptionally(ex);
        }
      };
      ScheduledFuture<?> scheduledTask = executorService.scheduleWithFixedDelay(pollTask, POLL_WAIT, POLL_WAIT, MILLISECONDS);
      // stop the scheduled job and cleanup server state quietly
      BiConsumer<MarginCalcResult, Throwable> cleanupFn = (result, resultEx) -> {
        scheduledTask.cancel(true);
        try {
          deleteCalculation(ccp, id);
        } catch (RuntimeException ex) {
          // ignore
        }
      };
      return resultFuture.whenComplete(cleanupFn);
    };

    return CompletableFuture.supplyAsync(createFn, executorService).thenCompose(pollingFn);
  }

  //-------------------------------------------------------------------------
  @Override
  public MarginWhatIfCalcResult calculateWhatIf(
      Ccp ccp,
      MarginCalcRequest request,
      List<PortfolioDataFile> deltaFiles) {

    String baseCalcId = createCalculation(ccp, request);
    ArrayList<PortfolioDataFile> combinedPortfolioData = new ArrayList<>();
    combinedPortfolioData.addAll(request.getPortfolioData());
    combinedPortfolioData.addAll(deltaFiles);

    MarginCalcRequest secondRequest = request.toBuilder()
        .portfolioData(combinedPortfolioData)
        .build();

    String deltaCalcId = createCalculation(ccp, secondRequest);
    MarginCalcResult baseResult = getCalculation(ccp, baseCalcId);
    MarginCalcResult deltaResult = getCalculation(ccp, deltaCalcId);
    while (MarginCalcResultStatus.PENDING.equals(baseResult.getStatus()) ||
        MarginCalcResultStatus.PENDING.equals(deltaResult.getStatus())) {
      try {
        Thread.sleep(POLL_WAIT);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
      if (MarginCalcResultStatus.PENDING.equals(baseResult.getStatus())) {
        baseResult = getCalculation(ccp, baseCalcId);
      }
      if (MarginCalcResultStatus.PENDING.equals(deltaResult.getStatus())) {
        deltaResult = getCalculation(ccp, deltaCalcId);
      }
    }
    // cleanup server state quietly
    try {
      deleteCalculation(ccp, baseCalcId);
      deleteCalculation(ccp, deltaCalcId);
    } catch (RuntimeException ex) {
      // ignore
    }

    return MarginWhatIfCalcResult.of(
        MarginCalcResultStatus.COMPLETED,
        request.getCalculationTypes(),
        deltaResult.getValuationDate(),
        deltaResult.getReportingCurrency(),
        deltaResult.getPortfolioItems(),
        baseResult.getMargin().orElseThrow(() -> new MarginException("No base margin found in response", "Invalid")),
        deltaResult.getMargin().orElseThrow(() -> new MarginException("No combined margin found in response", "Invalid")),
        deltaResult.getFailures());
  }

}
