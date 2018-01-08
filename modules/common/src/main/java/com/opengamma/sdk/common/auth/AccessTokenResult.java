/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.common.auth;

import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
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
  @PropertyDefinition(validate = "notBlank", alias = "access_token")
  private final String accessToken;
  /**
   * The token type.
   */
  @PropertyDefinition(validate = "notBlank", alias = "token_type")
  private final String tokenType;
  /**
   * The lifetime of the token in seconds.
   */
  @PropertyDefinition(alias = "expires_in")
  private final long expiresIn;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * <p>
   * It is intended that instances of {@code AccessTokenResult} are obtained
   * through the authentication process, not by calling this method.
   * The use case for this method is mocking authentication responses.
   * 
   * @param accessToken  the access token
   * @param tokenType  the token type
   * @param expiresIn  the expires in
   * @return the instance
   */
  public static AccessTokenResult of(String accessToken, String tokenType, long expiresIn) {
    return new AccessTokenResult(accessToken, tokenType, expiresIn);
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code AccessTokenResult}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return AccessTokenResult.Meta.INSTANCE;
  }

  static {
    MetaBean.register(AccessTokenResult.Meta.INSTANCE);
  }

  private AccessTokenResult(
      String accessToken,
      String tokenType,
      long expiresIn) {
    JodaBeanUtils.notBlank(accessToken, "accessToken");
    JodaBeanUtils.notBlank(tokenType, "tokenType");
    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
  }

  @Override
  public MetaBean metaBean() {
    return AccessTokenResult.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the access token.
   * @return the value of the property, not blank
   */
  public String getAccessToken() {
    return accessToken;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the token type.
   * @return the value of the property, not blank
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
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      AccessTokenResult other = (AccessTokenResult) obj;
      return JodaBeanUtils.equal(accessToken, other.accessToken) &&
          JodaBeanUtils.equal(tokenType, other.tokenType) &&
          (expiresIn == other.expiresIn);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(accessToken);
    hash = hash * 31 + JodaBeanUtils.hashCode(tokenType);
    hash = hash * 31 + JodaBeanUtils.hashCode(expiresIn);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("AccessTokenResult{");
    buf.append("accessToken").append('=').append(accessToken).append(',').append(' ');
    buf.append("tokenType").append('=').append(tokenType).append(',').append(' ');
    buf.append("expiresIn").append('=').append(JodaBeanUtils.toString(expiresIn));
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
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "accessToken",
        "tokenType",
        "expiresIn");

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

    /**
     * Restricted constructor.
     */
    private Builder() {
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
          expiresIn);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("AccessTokenResult.Builder{");
      buf.append("accessToken").append('=').append(JodaBeanUtils.toString(accessToken)).append(',').append(' ');
      buf.append("tokenType").append('=').append(JodaBeanUtils.toString(tokenType)).append(',').append(' ');
      buf.append("expiresIn").append('=').append(JodaBeanUtils.toString(expiresIn));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
