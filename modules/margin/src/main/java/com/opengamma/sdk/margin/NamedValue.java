/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

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
 * A name-value pair for returning results.
 */
@BeanDefinition(builderScope = "private", metaScope = "private", factoryName = "of")
public final class NamedValue implements ImmutableBean {

  /**
   * The name of the key.
   */
  @PropertyDefinition(validate = "notEmpty")
  private final String key;
  /**
   * The value associated with the key.
   */
  @PropertyDefinition
  private final double value;

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code NamedValue}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return NamedValue.Meta.INSTANCE;
  }

  static {
    MetaBean.register(NamedValue.Meta.INSTANCE);
  }

  /**
   * Obtains an instance.
   * @param key  the value of the property, not empty
   * @param value  the value of the property
   * @return the instance
   */
  public static NamedValue of(
      String key,
      double value) {
    return new NamedValue(
      key,
      value);
  }

  private NamedValue(
      String key,
      double value) {
    JodaBeanUtils.notEmpty(key, "key");
    this.key = key;
    this.value = value;
  }

  @Override
  public MetaBean metaBean() {
    return NamedValue.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the name of the key.
   * @return the value of the property, not empty
   */
  public String getKey() {
    return key;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the value associated with the key.
   * @return the value of the property
   */
  public double getValue() {
    return value;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      NamedValue other = (NamedValue) obj;
      return JodaBeanUtils.equal(key, other.key) &&
          JodaBeanUtils.equal(value, other.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(key);
    hash = hash * 31 + JodaBeanUtils.hashCode(value);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("NamedValue{");
    buf.append("key").append('=').append(JodaBeanUtils.toString(key)).append(',').append(' ');
    buf.append("value").append('=').append(JodaBeanUtils.toString(value));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code NamedValue}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code key} property.
     */
    private final MetaProperty<String> key = DirectMetaProperty.ofImmutable(
        this, "key", NamedValue.class, String.class);
    /**
     * The meta-property for the {@code value} property.
     */
    private final MetaProperty<Double> value = DirectMetaProperty.ofImmutable(
        this, "value", NamedValue.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "key",
        "value");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 106079:  // key
          return key;
        case 111972721:  // value
          return value;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends NamedValue> builder() {
      return new NamedValue.Builder();
    }

    @Override
    public Class<? extends NamedValue> beanType() {
      return NamedValue.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 106079:  // key
          return ((NamedValue) bean).getKey();
        case 111972721:  // value
          return ((NamedValue) bean).getValue();
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
   * The bean-builder for {@code NamedValue}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<NamedValue> {

    private String key;
    private double value;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 106079:  // key
          return key;
        case 111972721:  // value
          return value;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 106079:  // key
          this.key = (String) newValue;
          break;
        case 111972721:  // value
          this.value = (Double) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public NamedValue build() {
      return new NamedValue(
          key,
          value);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("NamedValue.Builder{");
      buf.append("key").append('=').append(JodaBeanUtils.toString(key)).append(',').append(' ');
      buf.append("value").append('=').append(JodaBeanUtils.toString(value));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
