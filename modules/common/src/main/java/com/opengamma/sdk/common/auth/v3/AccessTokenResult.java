/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth.v3;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

/**
 * The result returned to the client from an oauth2 request.
 * <p>
 * OAuth2 Access Token Response per https://tools.ietf.org/html/rfc6749#section-4.4
 */
@BeanDefinition(builderScope = "private", metaScope = "private")
public final class AccessTokenResult implements ImmutableBean {

  /**
   * The 'bearer' token type.
   */
  public static final String BEARER = "bearer";

  /**
   * The access token.
   */
  @PropertyDefinition(validate = "notNull", alias = "access_token")
  private final String accessToken;
  /**
   * The token type.
   */
  @PropertyDefinition(validate = "notNull", alias = "token_type")
  private final String tokenType;
  /**
   * The lifetime of the token in seconds.
   */
  @PropertyDefinition(alias = "expires_in")
  private final long expiresIn;

  /**
   * The API Credentials used to get the current {@link AccessTokenResult}.
   *  This is needed by the {@link com.opengamma.sdk.common.v3.ServiceInvoker.TokenInterceptor} class, to handle expired or missing tokens.
   */
  @PropertyDefinition
  private final transient ApiKeyCredentials credentials;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param accessToken  the access token
   * @param tokenType  the token type
   * @param expiresIn  the expires in
   * @param credentials the API credentials associated to the current {@link AccessTokenResult} object
   * @return the instance
   */
  public static AccessTokenResult of(String accessToken, String tokenType, long expiresIn, ApiKeyCredentials credentials) {
    return new AccessTokenResult(accessToken, tokenType, expiresIn, credentials);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code AccessTokenResult}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return AccessTokenResult.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(AccessTokenResult.Meta.INSTANCE);
  }

  private AccessTokenResult(
      String accessToken,
      String tokenType,
      long expiresIn,
      ApiKeyCredentials credentials) {
    JodaBeanUtils.notNull(accessToken, "accessToken");
    JodaBeanUtils.notNull(tokenType, "tokenType");
    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
    this.credentials = credentials;
  }

  @Override
  public MetaBean metaBean() {
    return AccessTokenResult.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the access token.
   * @return the value of the property, not null
   */
  public String getAccessToken() {
    return accessToken;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the token type.
   * @return the value of the property, not null
   */
  public String getTokenType() {
    return tokenType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the lifetime of the token in seconds.
   * @return the value of the property
   */
  public long getExpiresIn() {
    return expiresIn;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the API Credentials used to get the current {@link AccessTokenResult}.
   * This is needed by the {@link com.opengamma.sdk.common.v3.ServiceInvoker.TokenInterceptor} class, to handle expired or missing tokens.
   * @return the value of the property
   */
  public ApiKeyCredentials getCredentials() {
    return credentials;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      AccessTokenResult other = (AccessTokenResult) obj;
      return JodaBeanUtils.equal(accessToken, other.accessToken) &&
          JodaBeanUtils.equal(tokenType, other.tokenType) &&
          (expiresIn == other.expiresIn) &&
          JodaBeanUtils.equal(credentials, other.credentials);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(accessToken);
    hash = hash * 31 + JodaBeanUtils.hashCode(tokenType);
    hash = hash * 31 + JodaBeanUtils.hashCode(expiresIn);
    hash = hash * 31 + JodaBeanUtils.hashCode(credentials);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("AccessTokenResult{");
    buf.append("accessToken").append('=').append(accessToken).append(',').append(' ');
    buf.append("tokenType").append('=').append(tokenType).append(',').append(' ');
    buf.append("expiresIn").append('=').append(expiresIn).append(',').append(' ');
    buf.append("credentials").append('=').append(JodaBeanUtils.toString(credentials));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code AccessTokenResult}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code accessToken} property.
     */
    private final MetaProperty<String> accessToken = DirectMetaProperty.ofImmutable(
        this, "accessToken", AccessTokenResult.class, String.class);
    /**
     * The meta-property for the {@code tokenType} property.
     */
    private final MetaProperty<String> tokenType = DirectMetaProperty.ofImmutable(
        this, "tokenType", AccessTokenResult.class, String.class);
    /**
     * The meta-property for the {@code expiresIn} property.
     */
    private final MetaProperty<Long> expiresIn = DirectMetaProperty.ofImmutable(
        this, "expiresIn", AccessTokenResult.class, Long.TYPE);
    /**
     * The meta-property for the {@code credentials} property.
     */
    private final MetaProperty<ApiKeyCredentials> credentials = DirectMetaProperty.ofImmutable(
        this, "credentials", AccessTokenResult.class, ApiKeyCredentials.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "accessToken",
        "tokenType",
        "expiresIn",
        "credentials");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1042689291:  // accessToken
        case -1938933922:  // access_token (alias)
          return accessToken;
        case 141498579:  // tokenType
        case 101507520:  // token_type (alias)
          return tokenType;
        case 250196857:  // expiresIn
        case -833810928:  // expires_in (alias)
          return expiresIn;
        case 288957180:  // credentials
          return credentials;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends AccessTokenResult> builder() {
      return new AccessTokenResult.Builder();
    }

    @Override
    public Class<? extends AccessTokenResult> beanType() {
      return AccessTokenResult.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1042689291:  // accessToken
        case -1938933922:  // access_token (alias)
          return ((AccessTokenResult) bean).getAccessToken();
        case 141498579:  // tokenType
        case 101507520:  // token_type (alias)
          return ((AccessTokenResult) bean).getTokenType();
        case 250196857:  // expiresIn
        case -833810928:  // expires_in (alias)
          return ((AccessTokenResult) bean).getExpiresIn();
        case 288957180:  // credentials
          return ((AccessTokenResult) bean).getCredentials();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code AccessTokenResult}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<AccessTokenResult> {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private ApiKeyCredentials credentials;

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(meta());
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1042689291:  // accessToken
        case -1938933922:  // access_token (alias)
          return accessToken;
        case 141498579:  // tokenType
        case 101507520:  // token_type (alias)
          return tokenType;
        case 250196857:  // expiresIn
        case -833810928:  // expires_in (alias)
          return expiresIn;
        case 288957180:  // credentials
          return credentials;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1042689291:  // accessToken
        case -1938933922:  // access_token (alias)
          this.accessToken = (String) newValue;
          break;
        case 141498579:  // tokenType
        case 101507520:  // token_type (alias)
          this.tokenType = (String) newValue;
          break;
        case 250196857:  // expiresIn
        case -833810928:  // expires_in (alias)
          this.expiresIn = (Long) newValue;
          break;
        case 288957180:  // credentials
          this.credentials = (ApiKeyCredentials) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public AccessTokenResult build() {
      return new AccessTokenResult(
          accessToken,
          tokenType,
          expiresIn,
          credentials);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(160);
      buf.append("AccessTokenResult.Builder{");
      buf.append("accessToken").append('=').append(JodaBeanUtils.toString(accessToken)).append(',').append(' ');
      buf.append("tokenType").append('=').append(JodaBeanUtils.toString(tokenType)).append(',').append(' ');
      buf.append("expiresIn").append('=').append(JodaBeanUtils.toString(expiresIn)).append(',').append(' ');
      buf.append("credentials").append('=').append(JodaBeanUtils.toString(credentials));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
