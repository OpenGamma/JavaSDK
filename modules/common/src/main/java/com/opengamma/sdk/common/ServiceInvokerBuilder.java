/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.sdk.common.auth.AccessTokenResult;
import com.opengamma.sdk.common.auth.AuthClient;
import com.opengamma.sdk.common.auth.Credentials;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Builder used to create instances of the service invoker.
 */
public final class ServiceInvokerBuilder {

  /** Log, which uses ServiceInvoker.class for compatibility. */
  private static final Logger log = LoggerFactory.getLogger(ServiceInvoker.class);

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
   * HTTP header.
   */
  private static final String AUTHORIZATION = "Authorization";

  /** The credentials. */
  private Credentials credentials;
  /** The URL to call. */
  private HttpUrl serviceUrl = ServiceInvoker.SERVICE_URL;
  /** The HTTP client. */
  private OkHttpClient httpClient;
  /** The executor. */
  private ScheduledExecutorService executorService;
  /** The auth client factory. */
  private Function<ServiceInvoker, AuthClient> authClientFactory;
  /** Times to retry */
  private int retries = 1;

  //-------------------------------------------------------------------------
  /**
   * Creates an instance for the specified credentials.
   * <p>
   * Call {@link ServiceInvoker#builder(Credentials)} to create an instance.
   *
   * @param credentials  the credentials to use for authentication
   */
  ServiceInvokerBuilder(Credentials credentials) {
    this.credentials = Objects.requireNonNull(credentials, "credentials must not be null");
  }

  //-------------------------------------------------------------------------
  /**
   * Sets the URL of the server.
   * <p>
   * This allows the URL of the server to be changed from the standard one.
   * This is most useful in testing scenarios.
   *
   * @param serviceUrl  the URL of the service
   * @return this builder, for method chaining
   */
  public ServiceInvokerBuilder serviceUrl(HttpUrl serviceUrl) {
    this.serviceUrl = Objects.requireNonNull(serviceUrl, "serviceUrl must not be null");
    return this;
  }

  /**
   * Sets the HTTP client to use.
   * <p>
   * This allows the {@link OkHttpClient} to be changed from the standard one.
   * This might be used to setup a proxy.
   * If this method is used, interceptors will still be added for the user-agent
   * and authentication when {@link #build()} is called.
   * <p>
   * See {@link #httpClientFactory(Function)} for a method that provides access
   * to the recommended settings for the invoker.
   *
   * @param httpClient  the HTTP client to use
   * @return this builder, for method chaining
   */
  public ServiceInvokerBuilder httpClient(OkHttpClient httpClient) {
    this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    return this;
  }

  /**
   * Sets the number of retries for HTTP request that failed due to system/network issues.
   * <p>
   * This allows the client to cope with intermittent network failures, such as timeouts.
   *
   * @param retries  how many times to retry
   * @return this builder, for method chaining
   */
  public ServiceInvokerBuilder retries(int retries) {
    this.retries = retries;
    return this;
  }

  /**
   * Sets the HTTP client, provided with a partially complete builder.
   * <p>
   * This allows the {@link OkHttpClient} to be changed from the standard one.
   * This might be used to setup a proxy.
   * If this method is used, interceptors will still be added for the user-agent
   * and authentication when {@link #build()} is called.
   * <p>
   * Unlike {@link #httpClient(OkHttpClient)}, this method allows the standard settings
   * that the service invoker uses to be accessed. As such, the builder will be pre-populated
   * with a logging interceptor, read timeout and write timeout.
   *
   * @param httpClientFactory  the function that provides an HTTP client from a builder
   * @return this builder, for method chaining
   */
  public ServiceInvokerBuilder httpClientFactory(Function<OkHttpClient.Builder, OkHttpClient> httpClientFactory) {
    Objects.requireNonNull(httpClientFactory, "httpClientFactory must not be null");
    this.httpClient = httpClientFactory.apply(new OkHttpClient.Builder()
        .addInterceptor(new LoggingInterceptor())
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS));
    return this;
  }

  /**
   * Sets the executor service to use.
   * <p>
   * This allows the instance of {@link ScheduledExecutorService} to be changed from the standard one.
   * This is most useful in testing scenarios.
   * <p>
   * Note that the executor service passed in here will be closed if the {@link ServiceInvoker#close()}
   * method is called. If you are using a shared executor service, then you will need to ignore
   * the fact that {@code ServiceInvoker} is {@link AutoCloseable}.
   *
   * @param executor  the executor service
   * @return this builder, for method chaining
   */
  public ServiceInvokerBuilder executorService(ScheduledExecutorService executor) {
    this.executorService = Objects.requireNonNull(executor, "executor must not be null");
    return this;
  }

  /**
   * Sets the {@code AuthClient} factory.
   * <p>
   * This allows the instance of {@link AuthClient} to be changed from the standard one.
   * This is most useful in testing scenarios.
   *
   * @param authClientFactory  the factory for creating an {@code AuthClient}
   * @return this builder, for method chaining
   */
  public ServiceInvokerBuilder authClientFactory(Function<ServiceInvoker, AuthClient> authClientFactory) {
    this.authClientFactory = Objects.requireNonNull(authClientFactory, "authClientFactory must not be null");
    return this;
  }

  //-------------------------------------------------------------------------
  /**
   * Builds the service invoker, using the information provided.
   * <p>
   * This builder should not be used once this method is called.
   * 
   * @return the service invoker
   */
  public ServiceInvoker build() {
    // apply defaults
    if (httpClient == null) {
      httpClientFactory(builder -> builder.build());
    }
    if (executorService == null) {
      executorService = createExecutorService();
    }
    if (authClientFactory == null) {
      authClientFactory = inv -> AuthClient.of(inv);
    }
    // setup HttpClient
    TokenInterceptor tokenInterceptor = new TokenInterceptor();
    RetryInterceptor retryInterceptor = new RetryInterceptor();
    retryInterceptor.init(retries);
    httpClient = httpClient.newBuilder()
        .addInterceptor(tokenInterceptor)
        .addInterceptor(new UserAgentHeaderInterceptor())
        .addInterceptor(retryInterceptor)
        .build();
    // setup instance, creating a pure immutable ServiceInvoker, then using it
    // care should be taken when altering this code to ensure Java Memory Model semantics are considered
    ServiceInvoker invoker = new ServiceInvoker(serviceUrl, httpClient, executorService, retries);
    tokenInterceptor.init(authClientFactory.apply(invoker), credentials);
    return invoker;
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an executor service for use by the invoker.
   * <p>
   * This uses a thread pool with {@code Runtime.getRuntime().availableProcessors()} threads.
   * 
   * @return the executor service
   */
  private static ScheduledExecutorService createExecutorService() {
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

  //-------------------------------------------------------------------------
  // an interceptor that performs basic logging
  private static class LoggingInterceptor implements Interceptor {
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
  private static class UserAgentHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
      Request initialRequest = chain.request();
      Request modifiedRequest = initialRequest.newBuilder()
          .header("User-Agent", USER_AGENT)
          .build();
      return chain.proceed(modifiedRequest);
    }
  }

  //an interceptor that handles retries on System/network related exceptions (eg. timeout)
  private static class RetryInterceptor implements Interceptor {
    /** Times to retry */
    private int retryCount;

    // initializes the state, to ensure that ServiceInvoker is pure immutable wrt Java Memory Model
    void init(int retryCount) {
      this.retryCount = retryCount;
    }

    @Override
    public Response intercept(Chain chain) {
      Request request = chain.request();
      Response response = performRequest(chain, request);
      int retries = 1;
      while (response == null && retries < retryCount) {
        retries++;
        response = performRequest(chain, request);
      }
      if (response == null) {
        throw new IllegalStateException("Failed to perform request to given URL after " + retries + " retries: " + request.url().toString());
      }
      return response;
    }

    private Response performRequest(Chain chain, Request request) {
      Response response = null;
      try {
        response = chain.proceed(request);
      } catch (IOException ignored) {
      }
      return response;
    }
  }

  // an interceptor that adds the access token, and refreshes it when necessary
  private static class TokenInterceptor implements Interceptor {
    /** The lock protecting the token. */
    private final Lock lock = new ReentrantLock();
    /** The auth client. */
    private volatile AuthClient authClient;
    /** The credentials. */
    private volatile Credentials credentials;
    /** The current token. */
    private volatile AccessTokenResult token;

    // initializes the state, to ensure that ServiceInvoker is pure immutable wrt Java Memory Model
    void init(AuthClient authClient, Credentials credentials) {
      this.authClient = authClient;
      this.credentials = credentials;
    }

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

      // try to get a new token
      lock.lock();
      try {
        token = credentials.authenticate(authClient);
        Request modifiedRequest2 = initialRequest.newBuilder()
            .header(AUTHORIZATION, "Bearer " + token.getAccessToken())
            .build();
        return chain.proceed(modifiedRequest2);

      } finally {
        lock.unlock();
      }
    }
  }

}
