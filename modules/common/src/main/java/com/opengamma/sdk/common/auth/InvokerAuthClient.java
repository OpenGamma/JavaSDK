/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth;

import static com.opengamma.sdk.common.ServiceInvoker.MEDIA_JSON;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.joda.beans.ser.JodaBeanSer;

import com.opengamma.sdk.common.ServiceInvoker;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of the auth client.
 */
public final class InvokerAuthClient implements AuthClient {

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
  static InvokerAuthClient of(ServiceInvoker invoker) {
    return new InvokerAuthClient(invoker);
  }

  private InvokerAuthClient(ServiceInvoker invoker) {
    this.invoker = Objects.requireNonNull(invoker, "invoker must not be null");
  }

  //-------------------------------------------------------------------------
  @Override
  public AccessTokenResult authenticateApiKey(String apiKey, String secret) {
    String json = "{ \"grant_type\": \"client_credentials\"," +
        "\"client_id\": \"" +
        apiKey + "\"," +
        "\"client_secret\": \"" +
        secret +
        "\"" +
        "}";
    RequestBody requestBody = RequestBody.create(MEDIA_JSON, json);
    return authenticate("auth/v3/token", "API key: " + apiKey, requestBody, Credentials.ofApiKey(apiKey, secret));
  }

  @Override
  public AccessTokenResult authenticateApiKey(Credentials credentials) {
    return credentials.authenticate(this);
  }

  private AccessTokenResult authenticate(
      String url,
      String message,
      RequestBody formBody,
      Credentials credentials) {

    Request request = new Request.Builder()
        .url(invoker.getServiceUrl().resolve(url))
        .post(formBody)
        .header("Content-Type", MEDIA_JSON.toString())
        .header("Accept", MEDIA_JSON.toString())
        .build();

    try (Response response = invoker.getHttpClient().newCall(request).execute()) {
      if (response.code() == 401) {
        throw new AuthenticationException(
            "Authentication failed: Invalid credentials for " + message,
            401,
            "Invalid Credentials",
            "Invalid credentials for " + message);
      }
      if (response.code() == 403) {
        throw new AuthenticationException(
            "Authentication failed: Forbidden access for " + message,
            403,
            "Forbidden Access",
            "Forbidden access for " + message);
      }
      if (!response.isSuccessful()) {
        throw new AuthenticationException(
            "Authentication failed: " + response.code() + " for " + message,
            response.code(),
            response.message(),
            response.code() + " for " + message);
      }
      AccessTokenResult tokenResult = JodaBeanSer.COMPACT.jsonReader().read(
          response.body().string(),
          AccessTokenResult.class);
      return tokenResult.withCredentials(credentials);

    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

}
