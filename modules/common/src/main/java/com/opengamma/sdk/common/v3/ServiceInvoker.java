/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.v3;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import com.opengamma.sdk.common.auth.v3.AuthClient;
import com.opengamma.sdk.common.auth.v3.Credentials;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Invoker used to call the OpenGamma service.
 * <p>
 * An instance of this class should be obtained and used to invoke the remote service.
 * It may be used for multiple calls, in parallel if desired.
 * The functionality of the OpenGamma service is provided by interfaces such as {@code MarginClient},
 * which take an instance of this class when they are created.
 * <p>
 * The invoker is responsible for authentication, which happens using the specified {@link Credentials}.
 * The invoker also produces log files, if desired.
 * <p>
 * This class implements {@link AutoCloseable} and should be closed when no longer needed,
 * such as via the try-with-resources statement.
 */
public final class ServiceInvoker implements AutoCloseable {

  /**
   * The URL of the service.
   */
  public static final HttpUrl SERVICE_URL = HttpUrl.parse("https://api.opengamma.com");
  /**
   * JSON media type.
   */
  public static final MediaType MEDIA_JSON = MediaType.parse("application/json");
  /**
   * Form media type.
   */
  public static final MediaType MEDIA_FORM = MediaType.parse("application/x-www-form-urlencoded");

  /** URL to call. */
  private final HttpUrl serviceUrl;
  /** HTTP client. */
  private final OkHttpClient httpClient;
  /** Executor. */
  private final ScheduledExecutorService executor;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance for the specified credentials.
   * <p>
   * The executor will be initialized with {@link Runtime#availableProcessors()} threads.
   * <p>
   * The invoker is {@link AutoCloseable}, thus this method should typically
   * be called as part of a try-with-resources statement.
   *
   * @param credentials  the credentials to use for authentication
   * @return the invoker
   */
  public static ServiceInvoker of(Credentials credentials) {
    return builder(credentials).build();
  }

  /**
   * Returns a builder that can be used to create an instance of the invoker.
   * <p>
   * The invoker is {@link AutoCloseable}, thus the {@code build()} method should
   * typically be called as part of a try-with-resources statement.
   *
   * @param credentials  the credentials to use for authentication
   * @return the builder, used to further customize the invoker
   */
  public static ServiceInvokerBuilder builder(Credentials credentials) {
    return new ServiceInvokerBuilder(credentials);
  }

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance for the specified credentials and URL.
   * <p>
   * The executor will be initialized with {@link Runtime#availableProcessors()} threads.
   *
   * @param credentials  the credentials to use for authentication
   * @param serviceUrl  the URL of the service
   * @return the invoker
   * @deprecated Use the builder for greater clarity and flexibility
   */
  @Deprecated
  public static ServiceInvoker of(Credentials credentials, HttpUrl serviceUrl) {
    return new ServiceInvokerBuilder(credentials)
        .serviceUrl(serviceUrl)
        .build();
  }

  /**
   * Obtains an instance for the specified credentials and executor service.
   *
   * @param credentials  the credentials to use for authentication
   * @param executor  the executor service
   * @return the invoker
   * @deprecated Use the builder for greater clarity and flexibility
   */
  @Deprecated
  public static ServiceInvoker of(Credentials credentials, ScheduledExecutorService executor) {
    return new ServiceInvokerBuilder(credentials)
        .executorService(executor)
        .build();
  }

  /**
   * Obtains an instance for the specified credentials, URL and executor service.
   *
   * @param credentials  the credentials to use for authentication
   * @param serviceUrl  the URL of the service
   * @param executor  the executor service
   * @return the invoker
   * @deprecated Use the builder for greater clarity and flexibility
   */
  @Deprecated
  public static ServiceInvoker of(Credentials credentials, HttpUrl serviceUrl, ScheduledExecutorService executor) {
    return new ServiceInvokerBuilder(credentials)
        .serviceUrl(serviceUrl)
        .executorService(executor)
        .build();
  }

  /**
   * Obtains an instance for the specified credentials, URL and auth client.
   * <p>
   * This is primarily intended for testing scenarios, where a fake auth client is required.
   *
   * @param credentials  the credentials to use for authentication
   * @param serviceUrl  the URL of the service
   * @param authClient  the auth client
   * @return the invoker
   * @deprecated Use the builder for greater clarity and flexibility
   */
  @Deprecated
  public static ServiceInvoker of(Credentials credentials, HttpUrl serviceUrl, AuthClient authClient) {
    return new ServiceInvokerBuilder(credentials)
        .serviceUrl(serviceUrl)
        .authClientFactory(invoker -> authClient)
        .build();
  }

  //-------------------------------------------------------------------------
  // creates an instance
  ServiceInvoker(HttpUrl serviceUrl, OkHttpClient httpClient, ScheduledExecutorService executor) {
    this.serviceUrl = Objects.requireNonNull(serviceUrl, "serviceUrl must not be null");
    this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    this.executor = Objects.requireNonNull(executor, "executor must not be null");
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the server URL.
   *
   * @return the executor
   */
  public HttpUrl getServiceUrl() {
    return serviceUrl;
  }

  /**
   * Gets the HTTP client.
   *
   * @return the HTTP client
   */
  public OkHttpClient getHttpClient() {
    return httpClient;
  }

  /**
   * Gets the executor that can be used to poll.
   *
   * @return the executor
   */
  public ScheduledExecutorService getExecutor() {
    return executor;
  }

  //-------------------------------------------------------------------------
  /**
   * Closes access to the remote service.
   * <p>
   * This instance must not be used once this method is called.
   * <p>
   * This closes the executor, but not the HTTP client (as {@link OkHttpClient}
   * advises it should not normally be explicitly closed).
   */
  @Override
  public void close() {
    executor.shutdown();
  }

}
