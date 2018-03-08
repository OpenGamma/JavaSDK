/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth;

import static com.opengamma.sdk.common.ServiceInvoker.MEDIA_FORM;
import static com.opengamma.sdk.common.ServiceInvoker.MEDIA_JSON;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.joda.beans.ser.JodaBeanSer;
import org.joda.beans.ser.SerDeserializers;

import com.opengamma.sdk.common.ServiceInvoker;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of the auth client.
 *
 * @deprecated Since 1.3.0. Replaced by {@link com.opengamma.sdk.common.auth.v3.InvokerAuthClient} with an updated implementation.
 *   This class will be removed in future versions.
 */
@Deprecated
final class InvokerAuthClient implements AuthClient {

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
    RequestBody formBody = new FormBody.Builder()
        .add("grant_type", "client_credentials")
        .add("client_id", apiKey)
        .add("client_secret", secret)
        .build();
    return authenticate("auth/v1/tokenClientCredentials", "API key: " + apiKey, formBody);
  }

  @Override
  public AccessTokenResult authenticatePassword(String username, String password) {
    RequestBody formBody = new FormBody.Builder()
        .add("grant_type", "password")
        .add("username", username)
        .add("password", password)
        .build();
    return authenticate("auth/v1/tokenPassword", "username: " + username, formBody);
  }

  @Override
  public AccessTokenResult refreshToken(String refreshToken) {
    RequestBody formBody = new FormBody.Builder()
        .add("grant_type", "refresh_token")
        .add("refresh_token", refreshToken)
        .build();
    return authenticate("auth/v1/tokenRefresh", "refresh token", formBody);
  }

  private AccessTokenResult authenticate(String url, String message, RequestBody formBody) {
    Request request = new Request.Builder()
        .url(invoker.getServiceUrl().resolve(url))
        .post(formBody)
        .header("Content-Type", MEDIA_FORM.toString())
        .header("Accept", MEDIA_JSON.toString())
        .build();

    try (Response response = invoker.getHttpClient().newCall(request).execute()) {
      if (response.code() == 401) {
        throw new IllegalStateException("Authentication failed: Invalid credentials for " + message);
      }
      if (response.code() == 403) {
        throw new IllegalStateException("Authentication failed: Forbidden access for " + message);
      }
      if (!response.isSuccessful()) {
        throw new IllegalStateException("Authentication failed: " + response.code() + " for " + message);
      }
      return JodaBeanSer.COMPACT.withDeserializers(SerDeserializers.LENIENT)
          .jsonReader()
          .read(response.body().string(), AccessTokenResult.class);

    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

}
