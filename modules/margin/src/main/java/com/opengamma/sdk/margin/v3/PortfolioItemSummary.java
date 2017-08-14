/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.v3;

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
 * Result from the service.
 */
@BeanDefinition(builderScope = "private", metaScope = "private", factoryName = "of")
public final class PortfolioItemSummary implements ImmutableBean {

  /**
   * The identifier of the item.
   */
  @PropertyDefinition(validate = "notNull")
  private final String id;
  /**
   * The description of the product.
   */
  @PropertyDefinition(validate = "notNull")
  private final String product;
  /**
   * The description of the item.
   */
  @PropertyDefinition(validate = "notNull")
  private final String description;

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code PortfolioItemSummary}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return PortfolioItemSummary.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(PortfolioItemSummary.Meta.INSTANCE);
  }

  /**
   * Obtains an instance.
   * @param id  the value of the property, not null
   * @param product  the value of the property, not null
   * @param description  the value of the property, not null
   * @return the instance
   */
  public static PortfolioItemSummary of(
      String id,
      String product,
      String description) {
    return new PortfolioItemSummary(
      id,
      product,
      description);
  }

  private PortfolioItemSummary(
      String id,
      String product,
      String description) {
    JodaBeanUtils.notNull(id, "id");
    JodaBeanUtils.notNull(product, "product");
    JodaBeanUtils.notNull(description, "description");
    this.id = id;
    this.product = product;
    this.description = description;
  }

  @Override
  public MetaBean metaBean() {
    return PortfolioItemSummary.Meta.INSTANCE;
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
   * Gets the identifier of the item.
   * @return the value of the property, not null
   */
  public String getId() {
    return id;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the description of the product.
   * @return the value of the property, not null
   */
  public String getProduct() {
    return product;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the description of the item.
   * @return the value of the property, not null
   */
  public String getDescription() {
    return description;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      PortfolioItemSummary other = (PortfolioItemSummary) obj;
      return JodaBeanUtils.equal(id, other.id) &&
          JodaBeanUtils.equal(product, other.product) &&
          JodaBeanUtils.equal(description, other.description);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(id);
    hash = hash * 31 + JodaBeanUtils.hashCode(product);
    hash = hash * 31 + JodaBeanUtils.hashCode(description);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("PortfolioItemSummary{");
    buf.append("id").append('=').append(id).append(',').append(' ');
    buf.append("product").append('=').append(product).append(',').append(' ');
    buf.append("description").append('=').append(JodaBeanUtils.toString(description));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code PortfolioItemSummary}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code id} property.
     */
    private final MetaProperty<String> id = DirectMetaProperty.ofImmutable(
        this, "id", PortfolioItemSummary.class, String.class);
    /**
     * The meta-property for the {@code product} property.
     */
    private final MetaProperty<String> product = DirectMetaProperty.ofImmutable(
        this, "product", PortfolioItemSummary.class, String.class);
    /**
     * The meta-property for the {@code description} property.
     */
    private final MetaProperty<String> description = DirectMetaProperty.ofImmutable(
        this, "description", PortfolioItemSummary.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "id",
        "product",
        "description");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3355:  // id
          return id;
        case -309474065:  // product
          return product;
        case -1724546052:  // description
          return description;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends PortfolioItemSummary> builder() {
      return new PortfolioItemSummary.Builder();
    }

    @Override
    public Class<? extends PortfolioItemSummary> beanType() {
      return PortfolioItemSummary.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3355:  // id
          return ((PortfolioItemSummary) bean).getId();
        case -309474065:  // product
          return ((PortfolioItemSummary) bean).getProduct();
        case -1724546052:  // description
          return ((PortfolioItemSummary) bean).getDescription();
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
   * The bean-builder for {@code PortfolioItemSummary}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<PortfolioItemSummary> {

    private String id;
    private String product;
    private String description;

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
        case 3355:  // id
          return id;
        case -309474065:  // product
          return product;
        case -1724546052:  // description
          return description;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3355:  // id
          this.id = (String) newValue;
          break;
        case -309474065:  // product
          this.product = (String) newValue;
          break;
        case -1724546052:  // description
          this.description = (String) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public PortfolioItemSummary build() {
      return new PortfolioItemSummary(
          id,
          product,
          description);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("PortfolioItemSummary.Builder{");
      buf.append("id").append('=').append(JodaBeanUtils.toString(id)).append(',').append(' ');
      buf.append("product").append('=').append(JodaBeanUtils.toString(product)).append(',').append(' ');
      buf.append("description").append('=').append(JodaBeanUtils.toString(description));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
