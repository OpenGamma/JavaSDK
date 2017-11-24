/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.v3;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.sdk.common.Version;
import com.opengamma.sdk.common.auth.v3.AccessTokenResult;
import com.opengamma.sdk.common.auth.v3.AuthClient;
import com.opengamma.sdk.common.auth.v3.AuthenticationException;
import com.opengamma.sdk.common.auth.v3.Credentials;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
   * The user agent to send with requests.
   */
  private static final String USER_AGENT;

  static {
    String userAgentHeader = "opengamma-sdk-java/" + Version.getVersionString();
    try {
      Properties systemProperties = System.getProperties();
      userAgentHeader += " (" +
          systemProperties.getProperty("os.name") +
          "; " +
          systemProperties.getProperty("os.version") +
          "; " +
          systemProperties.getProperty("os.arch") +
          ") Java " +
          systemProperties.getProperty("java.version") +
          " (" +
          systemProperties.getProperty("java.vendor") +
          ")";
    } catch (SecurityException ex) {
      //ignored
    }
    USER_AGENT = userAgentHeader;
  }
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

  /** Log. */
  private static final Logger log = LoggerFactory.getLogger(ServiceInvoker.class);
  /**
   * HTTP header.
   */
  private static final String AUTHORIZATION = "Authorization";

  /** URL to call. */
  private final HttpUrl serviceUrl;
  /** HTTP client. */
  private final OkHttpClient httpClient;
  /** Executor. */
  private final ScheduledExecutorService executor;
  /** Auth client. */
  private final AuthClient authClient;
  /** Current token. */
  private volatile AccessTokenResult token;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance for the specified credentials.
   * <p>
   * The executor will be initialized with {@link Runtime#availableProcessors()} threads.
   *
   * @param credentials  the credentials to use for authentication
   * @return the invoker
   */
  public static ServiceInvoker of(Credentials credentials) {
    return new ServiceInvoker(credentials, SERVICE_URL, createExecutor(), null);
  }

  /**
   * Obtains an instance for the specified credentials and executor service.
   *
   * @param credentials  the credentials to use for authentication
   * @param executor  the executor service
   * @return the invoker
   */
  public static ServiceInvoker of(Credentials credentials, ScheduledExecutorService executor) {
    return new ServiceInvoker(credentials, SERVICE_URL, executor, null);
  }

  /**
   * Obtains an instance for the specified credentials and URL.
   * <p>
   * The executor will be initialized with {@link Runtime#availableProcessors()} threads.
   *
   * @param credentials  the credentials to use for authentication
   * @param serviceUrl  the URL of the service
   * @return the invoker
   */
  public static ServiceInvoker of(Credentials credentials, HttpUrl serviceUrl) {
    return new ServiceInvoker(credentials, serviceUrl, createExecutor(), null);
  }

  /**
   * Obtains an instance for the specified credentials, URL and executor service.
   *
   * @param credentials  the credentials to use for authentication
   * @param serviceUrl  the URL of the service
   * @param executor  the executor service
   * @return the invoker
   */
  public static ServiceInvoker of(Credentials credentials, HttpUrl serviceUrl, ScheduledExecutorService executor) {
    return new ServiceInvoker(credentials, serviceUrl, executor, null);
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
   */
  public static ServiceInvoker of(Credentials credentials, HttpUrl serviceUrl, AuthClient authClient) {
    return new ServiceInvoker(credentials, serviceUrl, createExecutor(), authClient);
  }

  // creates a simple executor for polling
  private static ScheduledExecutorService createExecutor() {
    ThreadFactory threadFactory = r -> {
      Thread t = Executors.defaultThreadFactory().newThread(r);
      t.setName("ServiceInvoker-" + t.getName());
      t.setDaemon(true);
      return t;
    };
    ScheduledExecutorService executor =
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
    return executor;
  }

  // creates an instance
  private ServiceInvoker(
      Credentials credentials,
      HttpUrl serviceUrl,
      ScheduledExecutorService executor,
      AuthClient authClient) {

    Objects.requireNonNull(credentials, "credentials must not be null");
    Objects.requireNonNull(serviceUrl, "serviceUrl must not be null");
    Objects.requireNonNull(executor, "executor must not be null");
    this.serviceUrl = serviceUrl;
    this.httpClient = new OkHttpClient.Builder()
        .addInterceptor(new LoggingInterceptor())
        .addInterceptor(new TokenInterceptor())
        .addInterceptor(new UserAgentHeaderInterceptor())
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build();
    this.executor = executor;
    this.authClient = (authClient != null ? authClient : AuthClient.of(this));
    this.token = credentials.authenticate(this.authClient);
  }

  // an interceptor that performs basic logging
  private class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      log.debug("Call service: {} {}", request.method(), request.url());
      Response response = chain.proceed(request);
      log.debug("Service responded: {}", response.code());
      return response;
    }
  }

  //An interceptor that adds the User-Agent header and exposes useful information about the SDK and runtime.
  private class UserAgentHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
      Request initialRequest = chain.request();
      Request modifiedRequest = initialRequest.newBuilder()
          .header("User-Agent", USER_AGENT)
          .build();
      return chain.proceed(modifiedRequest);
    }
  }

  // an interceptor that adds the access token, and refreshes it when necessary
  private class TokenInterceptor implements Interceptor {
    private final Lock lock = new ReentrantLock();

    @Override
    public Response intercept(Chain chain) throws IOException {
      // do nothing for auth
      Request initialRequest = chain.request();
      if (initialRequest.url().pathSegments().contains("auth")) {
        return chain.proceed(initialRequest);
      }

      // try using the current access token, unless not present
      AccessTokenResult copyOfToken = token;
      if (copyOfToken != null) {
        log.trace("Add token: {}", copyOfToken.getAccessToken());
        Request modifiedRequest = initialRequest.newBuilder()
            .header(AUTHORIZATION, "Bearer " + copyOfToken.getAccessToken())
            .build();
        Response response = chain.proceed(modifiedRequest);
        if (response.code() != 401) {
          return response;
        }
      }

      // try to refresh the token
      lock.lock();
      try {
        if (token == null) {
          throw new AuthenticationException("Authentication failed: Unable to retry", "Authentication Failed");
        }
        token = token.getCredentials().authenticate(authClient);
        Request modifiedRequest2 = initialRequest.newBuilder()
            .header(AUTHORIZATION, "Bearer " + token.getAccessToken())
            .build();
        return chain.proceed(modifiedRequest2);

      } finally {
        lock.unlock();
      }
    }
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
   */
  @Override
  public void close() {
    executor.shutdown();
  }
}
