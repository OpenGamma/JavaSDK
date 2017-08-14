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
 * API key and secret credentials, used to authenticate with the service.
 * <p>
 * This is the OAuth2 "client credentials" concept.
 */
@BeanDefinition(builderScope = "private", metaScope = "private", factoryName = "of")
final class ApiKeyCredentials implements Credentials, ImmutableBean {

  /**
   * The API Key ID with which to authenticate
   */
  @PropertyDefinition(validate = "notEmpty", get = "")
  private final String apiKey;

  /**
   * The secret corresponding to the id with which to authenticate
   */
  @PropertyDefinition(validate = "notEmpty", get = "")
  private final String secret;

  //-------------------------------------------------------------------------
  @Override
  public AccessTokenResult authenticate(AuthClient client) {
    return client.authenticateApiKey(apiKey, secret);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ApiKeyCredentials}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return ApiKeyCredentials.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ApiKeyCredentials.Meta.INSTANCE);
  }

  /**
   * Obtains an instance.
   * @param apiKey  the value of the property, not empty
   * @param secret  the value of the property, not empty
   * @return the instance
   */
  public static ApiKeyCredentials of(
      String apiKey,
      String secret) {
    return new ApiKeyCredentials(
      apiKey,
      secret);
  }

  private ApiKeyCredentials(
      String apiKey,
      String secret) {
    JodaBeanUtils.notEmpty(apiKey, "apiKey");
    JodaBeanUtils.notEmpty(secret, "secret");
    this.apiKey = apiKey;
    this.secret = secret;
  }

  @Override
  public MetaBean metaBean() {
    return ApiKeyCredentials.Meta.INSTANCE;
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
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ApiKeyCredentials other = (ApiKeyCredentials) obj;
      return JodaBeanUtils.equal(apiKey, other.apiKey) &&
          JodaBeanUtils.equal(secret, other.secret);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(apiKey);
    hash = hash * 31 + JodaBeanUtils.hashCode(secret);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("ApiKeyCredentials{");
    buf.append("apiKey").append('=').append(apiKey).append(',').append(' ');
    buf.append("secret").append('=').append(JodaBeanUtils.toString(secret));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ApiKeyCredentials}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code apiKey} property.
     */
    private final MetaProperty<String> apiKey = DirectMetaProperty.ofImmutable(
        this, "apiKey", ApiKeyCredentials.class, String.class);
    /**
     * The meta-property for the {@code secret} property.
     */
    private final MetaProperty<String> secret = DirectMetaProperty.ofImmutable(
        this, "secret", ApiKeyCredentials.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "apiKey",
        "secret");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1411301915:  // apiKey
          return apiKey;
        case -906277200:  // secret
          return secret;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ApiKeyCredentials> builder() {
      return new ApiKeyCredentials.Builder();
    }

    @Override
    public Class<? extends ApiKeyCredentials> beanType() {
      return ApiKeyCredentials.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1411301915:  // apiKey
          return ((ApiKeyCredentials) bean).apiKey;
        case -906277200:  // secret
          return ((ApiKeyCredentials) bean).secret;
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
   * The bean-builder for {@code ApiKeyCredentials}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<ApiKeyCredentials> {

    private String apiKey;
    private String secret;

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
        case -1411301915:  // apiKey
          return apiKey;
        case -906277200:  // secret
          return secret;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1411301915:  // apiKey
          this.apiKey = (String) newValue;
          break;
        case -906277200:  // secret
          this.secret = (String) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public ApiKeyCredentials build() {
      return new ApiKeyCredentials(
          apiKey,
          secret);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("ApiKeyCredentials.Builder{");
      buf.append("apiKey").append('=').append(JodaBeanUtils.toString(apiKey)).append(',').append(' ');
      buf.append("secret").append('=').append(JodaBeanUtils.toString(secret));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
