/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.v3;

import static com.opengamma.sdk.common.v3.ServiceInvoker.MEDIA_JSON;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joda.beans.ser.JodaBeanSer;
import org.joda.beans.ser.SerDeserializers;

import com.opengamma.sdk.common.v3.ServiceInvoker;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of the margin client.
 */
public final class InvokerMarginClient implements MarginClient {

  /**
   * Sleep for 500ms between polls.
   */
  private static final long POLL_WAIT = 500;
  /**
   * Timeout for polling the result.
   */
  private static final TemporalAmount POLL_TIMEOUT = Duration.ofMinutes(30);
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
  public static InvokerMarginClient of(ServiceInvoker invoker) {
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
        ErrorMessage errorMessage = parseError(response);
        throw new IllegalStateException("Request failed. Reason: " + errorMessage.getReason() + ", status code: " +
            response.code() + ", message: " + errorMessage.getMessage());
      }
      return JodaBeanSer.COMPACT.withDeserializers(SerDeserializers.LENIENT)
          .jsonReader()
          .read(response.body().string(), CcpsResult.class);

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
        ErrorMessage errorMessage = parseError(response);
        throw new IllegalStateException("Request failed. Reason: " + errorMessage.getReason() + ", status code: " +
            response.code() + ", message: " + errorMessage.getMessage());
      }
      return JodaBeanSer.COMPACT.withDeserializers(SerDeserializers.LENIENT)
          .jsonReader()
          .read(response.body().string(), CcpInfo.class);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public String createCalculation(Ccp ccp, MarginCalcRequest calcRequest) {
    String text = JodaBeanSer.COMPACT.jsonWriter().write(calcRequest, false);
    RequestBody body = RequestBody.create(MEDIA_JSON, text);
    Request request = new Request.Builder()
        .url(invoker.getServiceUrl().resolve("margin/v3/ccps/" + ccp.name().toLowerCase(Locale.ENGLISH) + "/calculations"))
        .post(body)
        .header("Content-Type", MEDIA_JSON.toString())
        .header("Accept", MEDIA_JSON.toString())
        .build();

    try (Response response = invoker.getHttpClient().newCall(request).execute()) {
      if (response.code() != 202) {
        ErrorMessage errorMessage = parseError(response);
        throw new IllegalStateException("Request failed. Reason: " + errorMessage.getReason() + ", status code: " +
            response.code() + ", message: " + errorMessage.getMessage());
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
        ErrorMessage errorMessage = parseError(response);
        throw new IllegalStateException("Request failed. Reason: " + errorMessage.getReason() + ", status code: " +
            response.code() + ", message: " + errorMessage.getMessage());
      }
      return JodaBeanSer.COMPACT.withDeserializers(SerDeserializers.LENIENT)
          .jsonReader()
          .read(response.body().string(), MarginCalcResult.class);

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
        ErrorMessage errorMessage = parseError(response);
        throw new IllegalStateException("Request failed. Reason: " + errorMessage.getReason() + ", status code: " +
            response.code() + ", message: " + errorMessage.getMessage());
      }

    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  // avoid errors when processing errors
  private ErrorMessage parseError(Response response) throws IOException {
    try {
      return JodaBeanSer.COMPACT.jsonReader().read(response.body().string(), ErrorMessage.class);
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
  public MultiCcpMarginCalcResult calculate(
      List<Ccp> ccps,
      MarginCalcRequest request) throws ExecutionException, InterruptedException {

    Map<Ccp, CompletableFuture<MarginCalcResult>> completableFutures = ccps.parallelStream()
        .collect(Collectors.toMap(Function.identity(), ccp -> calculateAsync(ccp, request)));

    List<MarginCalcResult> multiCcpMarginResults = new ArrayList<>();
    HashMap<Ccp, MarginSummary> multiCcpMarginSummaries = new HashMap<>();
    for (Map.Entry<Ccp, CompletableFuture<MarginCalcResult>> entry : completableFutures.entrySet()) {
      MarginCalcResult marginCalcResult = entry.getValue().get();

      multiCcpMarginResults.add(marginCalcResult);
      if (marginCalcResult.getMargin().isPresent()) {
        multiCcpMarginSummaries.put(entry.getKey(), marginCalcResult.getMargin().get());
      }
    }

    List<MarginError> aggregatedFailures = multiCcpMarginResults.stream()
        .flatMap(x -> x.getFailures().stream())
        .collect(Collectors.toList());

    return MultiCcpMarginCalcResult.builder()
        .status(MarginCalcResultStatus.COMPLETED) //The status is always COMPLETED at this stage
        .type(request.getType())
        .valuationDate(request.getValuationDate())
        .reportingCurrency(request.getReportingCurrency())
        .applyClientMultiplier(request.isApplyClientMultiplier())
        .margin(multiCcpMarginSummaries)
        .failures(aggregatedFailures)
        .build();
  }

  @Override
  public MultiCcpMarginCalcResult calculateForAllCcps(MarginCalcRequest request) throws ExecutionException, InterruptedException {
    List<Ccp> availableCcps = listCcps().getCcpNames().stream()
        .map(Ccp::of)
        .collect(Collectors.toList());

    return calculate(availableCcps, request);
  }

  @Override
  public CompletableFuture<MarginCalcResult> calculateAsync(Ccp ccp, MarginCalcRequest request) {
    CompletableFuture<MarginCalcResult> resultPromise = new CompletableFuture<>();

    Runnable r = () -> {
      String calcId = createCalculation(ccp, request);
      Instant timeout = Instant.now().plus(POLL_TIMEOUT);
      Runnable pollTask = () -> {
        MarginCalcResult calcResult = getCalculation(ccp, calcId);
        if (calcResult.getStatus() == MarginCalcResultStatus.COMPLETED) {
          resultPromise.complete(calcResult);
          return;
        }
        if (Instant.now().isAfter(timeout)) {
          resultPromise.completeExceptionally(new IllegalStateException("Timed out while polling margin service"));
          return;
        }
      };
      ScheduledFuture<?> scheduledTask =
          invoker.getExecutor().scheduleWithFixedDelay(pollTask, POLL_WAIT, POLL_WAIT, TimeUnit.MILLISECONDS);
      resultPromise.whenComplete((res, ex) -> {
        scheduledTask.cancel(true);
        // cleanup server state quietly
        try {
          deleteCalculation(ccp, calcId);
        } catch (RuntimeException ex2) {
          // ignore
        }

      });
    };
    invoker.getExecutor().execute(r);

    return resultPromise;
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
        request.getType(),
        deltaResult.getValuationDate(),
        deltaResult.getReportingCurrency(),
        deltaResult.getPortfolioItems(),
        baseResult.getMargin().orElseThrow(IllegalStateException::new),
        deltaResult.getMargin().orElseThrow(IllegalStateException::new),
        deltaResult.getFailures());
  }
}
