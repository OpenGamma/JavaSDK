/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
 * The summary of the calculated margin.
 */
@BeanDefinition(builderScope = "private", metaScope = "private", factoryName = "of")
public final class MarginSummary implements ImmutableBean {

  /**
   * The total margin, expressed in the reporting currency.
   */
  @PropertyDefinition
  private final double margin;
  /**
   * The CCP-specific breakdown of the initial margin.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<NamedValue> marginDetails;
  /**
   * The standardized breakdown of the initial margin.
   * <p>
   * The contents of {@link #getMarginDetails()} are CCP-specific.
   * This breakdown summarizes the values under three standardized headings.
   */
  @PropertyDefinition(validate = "notNull")
  private final MarginBreakdown breakdown;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param margin  the total margin
   * @param marginDetails  the margin details
   * @return the instance
   * @deprecated Use the 3-arg version of this method
   *   (it is intended that the SDK creates instances of this class, your code should only create instances in tests)
   */
  @Deprecated
  public static MarginSummary of(double margin, List<NamedValue> marginDetails) {
    return new MarginSummary(
        margin,
        marginDetails,
        MarginBreakdown.of(margin, margin, 0, 0));
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code MarginSummary}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return MarginSummary.Meta.INSTANCE;
  }

  static {
    MetaBean.register(MarginSummary.Meta.INSTANCE);
  }

  /**
   * Obtains an instance.
   * @param margin  the value of the property
   * @param marginDetails  the value of the property, not null
   * @param breakdown  the value of the property, not null
   * @return the instance
   */
  public static MarginSummary of(
      double margin,
      List<NamedValue> marginDetails,
      MarginBreakdown breakdown) {
    return new MarginSummary(
      margin,
      marginDetails,
      breakdown);
  }

  private MarginSummary(
      double margin,
      List<NamedValue> marginDetails,
      MarginBreakdown breakdown) {
    JodaBeanUtils.notNull(marginDetails, "marginDetails");
    JodaBeanUtils.notNull(breakdown, "breakdown");
    this.margin = margin;
    this.marginDetails = Collections.unmodifiableList(new ArrayList<>(marginDetails));
    this.breakdown = breakdown;
  }

  @Override
  public MetaBean metaBean() {
    return MarginSummary.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the total margin, expressed in the reporting currency.
   * @return the value of the property
   */
  public double getMargin() {
    return margin;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the CCP-specific breakdown of the initial margin.
   * @return the value of the property, not null
   */
  public List<NamedValue> getMarginDetails() {
    return marginDetails;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the standardized breakdown of the initial margin.
   * <p>
   * The contents of {@link #getMarginDetails()} are CCP-specific.
   * This breakdown summarizes the values under three standardized headings.
   * @return the value of the property, not null
   */
  public MarginBreakdown getBreakdown() {
    return breakdown;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      MarginSummary other = (MarginSummary) obj;
      return JodaBeanUtils.equal(margin, other.margin) &&
          JodaBeanUtils.equal(marginDetails, other.marginDetails) &&
          JodaBeanUtils.equal(breakdown, other.breakdown);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(margin);
    hash = hash * 31 + JodaBeanUtils.hashCode(marginDetails);
    hash = hash * 31 + JodaBeanUtils.hashCode(breakdown);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("MarginSummary{");
    buf.append("margin").append('=').append(JodaBeanUtils.toString(margin)).append(',').append(' ');
    buf.append("marginDetails").append('=').append(JodaBeanUtils.toString(marginDetails)).append(',').append(' ');
    buf.append("breakdown").append('=').append(JodaBeanUtils.toString(breakdown));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code MarginSummary}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code margin} property.
     */
    private final MetaProperty<Double> margin = DirectMetaProperty.ofImmutable(
        this, "margin", MarginSummary.class, Double.TYPE);
    /**
     * The meta-property for the {@code marginDetails} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<NamedValue>> marginDetails = DirectMetaProperty.ofImmutable(
        this, "marginDetails", MarginSummary.class, (Class) List.class);
    /**
     * The meta-property for the {@code breakdown} property.
     */
    private final MetaProperty<MarginBreakdown> breakdown = DirectMetaProperty.ofImmutable(
        this, "breakdown", MarginSummary.class, MarginBreakdown.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "margin",
        "marginDetails",
        "breakdown");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1081309778:  // margin
          return margin;
        case 1113711796:  // marginDetails
          return marginDetails;
        case -1897470431:  // breakdown
          return breakdown;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends MarginSummary> builder() {
      return new MarginSummary.Builder();
    }

    @Override
    public Class<? extends MarginSummary> beanType() {
      return MarginSummary.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1081309778:  // margin
          return ((MarginSummary) bean).getMargin();
        case 1113711796:  // marginDetails
          return ((MarginSummary) bean).getMarginDetails();
        case -1897470431:  // breakdown
          return ((MarginSummary) bean).getBreakdown();
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
   * The bean-builder for {@code MarginSummary}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<MarginSummary> {

    private double margin;
    private List<NamedValue> marginDetails = Collections.emptyList();
    private MarginBreakdown breakdown;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1081309778:  // margin
          return margin;
        case 1113711796:  // marginDetails
          return marginDetails;
        case -1897470431:  // breakdown
          return breakdown;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1081309778:  // margin
          this.margin = (Double) newValue;
          break;
        case 1113711796:  // marginDetails
          this.marginDetails = (List<NamedValue>) newValue;
          break;
        case -1897470431:  // breakdown
          this.breakdown = (MarginBreakdown) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public MarginSummary build() {
      return new MarginSummary(
          margin,
          marginDetails,
          breakdown);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("MarginSummary.Builder{");
      buf.append("margin").append('=').append(JodaBeanUtils.toString(margin)).append(',').append(' ');
      buf.append("marginDetails").append('=').append(JodaBeanUtils.toString(marginDetails)).append(',').append(' ');
      buf.append("breakdown").append('=').append(JodaBeanUtils.toString(breakdown));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
