/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutablePreBuild;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

/**
 * The per-trade valuation of the portfolio.
 */
@BeanDefinition(builderScope = "private", metaScope = "private", factoryName = "of")
public final class TradeValuations implements ImmutableBean {

  /**
   * The total present value for all trades.
   * <p>
   * The value is expressed in the reporting currency.
   * Note that this is the sum of all the trade-level present values.
   */
  @PropertyDefinition
  private final double totalPresentValue;
  /**
   * The total delta for all trades.
   * <p>
   * The value is expressed in the reporting currency.
   * Note that this is the sum of all the trade-level deltas.
   */
  @PropertyDefinition(get = "optional")
  private final Double totalDelta;
  /**
   * The per-curve delta.
   * <p>
   * The values are expressed in the currency of the curve.
   * Note that these are the sums of all the trade-level delta curves.
   * <p>
   * Populated only when margin calculation type {@link MarginCalcType#DELTA DELTA} has been requested.
   */
  @PropertyDefinition(get = "optional")
  private final List<TradeCurveSensitivity> bucketedDelta;
  /**
   * The total gamma for all trades.
   * <p>
   * The value is expressed in the reporting currency.
   * Note that this is the sum of all the trade-level gamma.
   */
  @PropertyDefinition(get = "optional")
  private final Double totalGamma;
  /**
   * The per-curve gamma.
   * <p>
   * The values are expressed in the currency of the curve.
   * Note that these are the sums of all the trade-level gamma curves.
   * <p>
   * Populated only when margin calculation type {@link MarginCalcType#GAMMA GAMMA} has been requested.
   */
  @PropertyDefinition(get = "optional")
  private final List<TradeCurveSensitivity> bucketedGamma;
  /**
   * The per-trade valuation.
   * <p>
   * An entry will be added for each trade where the valuation was calculated.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<TradeValuation> trades;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param totalPresentValue  the value of the property
   * @param totalDelta  the value of the property
   * @param totalGamma  the value of the property
   * @param trades  the value of the property, not null
   * @return the instance
   * @deprecated Use variation with 6 arguments
   */
  @Deprecated
  public static TradeValuations of(
      double totalPresentValue,
      Double totalDelta,
      Double totalGamma,
      List<TradeValuation> trades) {

    return new TradeValuations(totalPresentValue, totalDelta, null, totalGamma, null, trades);
  }

  @ImmutablePreBuild
  private static void preBuild(Builder builder) {
    if (builder.totalDelta == null) {
      builder.bucketedDelta = null;
    }
    if (builder.totalGamma == null) {
      builder.bucketedGamma = null;
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code TradeValuations}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return TradeValuations.Meta.INSTANCE;
  }

  static {
    MetaBean.register(TradeValuations.Meta.INSTANCE);
  }

  /**
   * Obtains an instance.
   * @param totalPresentValue  the value of the property
   * @param totalDelta  the value of the property
   * @param bucketedDelta  the value of the property
   * @param totalGamma  the value of the property
   * @param bucketedGamma  the value of the property
   * @param trades  the value of the property, not null
   * @return the instance
   */
  public static TradeValuations of(
      double totalPresentValue,
      Double totalDelta,
      List<TradeCurveSensitivity> bucketedDelta,
      Double totalGamma,
      List<TradeCurveSensitivity> bucketedGamma,
      List<TradeValuation> trades) {
    return new TradeValuations(
      totalPresentValue,
      totalDelta,
      bucketedDelta,
      totalGamma,
      bucketedGamma,
      trades);
  }

  private TradeValuations(
      double totalPresentValue,
      Double totalDelta,
      List<TradeCurveSensitivity> bucketedDelta,
      Double totalGamma,
      List<TradeCurveSensitivity> bucketedGamma,
      List<TradeValuation> trades) {
    JodaBeanUtils.notNull(trades, "trades");
    this.totalPresentValue = totalPresentValue;
    this.totalDelta = totalDelta;
    this.bucketedDelta = (bucketedDelta != null ? Collections.unmodifiableList(new ArrayList<>(bucketedDelta)) : null);
    this.totalGamma = totalGamma;
    this.bucketedGamma = (bucketedGamma != null ? Collections.unmodifiableList(new ArrayList<>(bucketedGamma)) : null);
    this.trades = Collections.unmodifiableList(new ArrayList<>(trades));
  }

  @Override
  public MetaBean metaBean() {
    return TradeValuations.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the total present value for all trades.
   * <p>
   * The value is expressed in the reporting currency.
   * Note that this is the sum of all the trade-level present values.
   * @return the value of the property
   */
  public double getTotalPresentValue() {
    return totalPresentValue;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the total delta for all trades.
   * <p>
   * The value is expressed in the reporting currency.
   * Note that this is the sum of all the trade-level deltas.
   * @return the optional value of the property, not null
   */
  public OptionalDouble getTotalDelta() {
    return totalDelta != null ? OptionalDouble.of(totalDelta) : OptionalDouble.empty();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the per-curve delta.
   * <p>
   * The values are expressed in the currency of the curve.
   * Note that these are the sums of all the trade-level delta curves.
   * <p>
   * Populated only when margin calculation type {@link MarginCalcType#DELTA DELTA} has been requested.
   * @return the optional value of the property, not null
   */
  public Optional<List<TradeCurveSensitivity>> getBucketedDelta() {
    return Optional.ofNullable(bucketedDelta);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the total gamma for all trades.
   * <p>
   * The value is expressed in the reporting currency.
   * Note that this is the sum of all the trade-level gamma.
   * @return the optional value of the property, not null
   */
  public OptionalDouble getTotalGamma() {
    return totalGamma != null ? OptionalDouble.of(totalGamma) : OptionalDouble.empty();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the per-curve gamma.
   * <p>
   * The values are expressed in the currency of the curve.
   * Note that these are the sums of all the trade-level gamma curves.
   * <p>
   * Populated only when margin calculation type {@link MarginCalcType#GAMMA GAMMA} has been requested.
   * @return the optional value of the property, not null
   */
  public Optional<List<TradeCurveSensitivity>> getBucketedGamma() {
    return Optional.ofNullable(bucketedGamma);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the per-trade valuation.
   * <p>
   * An entry will be added for each trade where the valuation was calculated.
   * @return the value of the property, not null
   */
  public List<TradeValuation> getTrades() {
    return trades;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      TradeValuations other = (TradeValuations) obj;
      return JodaBeanUtils.equal(totalPresentValue, other.totalPresentValue) &&
          JodaBeanUtils.equal(totalDelta, other.totalDelta) &&
          JodaBeanUtils.equal(bucketedDelta, other.bucketedDelta) &&
          JodaBeanUtils.equal(totalGamma, other.totalGamma) &&
          JodaBeanUtils.equal(bucketedGamma, other.bucketedGamma) &&
          JodaBeanUtils.equal(trades, other.trades);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(totalPresentValue);
    hash = hash * 31 + JodaBeanUtils.hashCode(totalDelta);
    hash = hash * 31 + JodaBeanUtils.hashCode(bucketedDelta);
    hash = hash * 31 + JodaBeanUtils.hashCode(totalGamma);
    hash = hash * 31 + JodaBeanUtils.hashCode(bucketedGamma);
    hash = hash * 31 + JodaBeanUtils.hashCode(trades);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(224);
    buf.append("TradeValuations{");
    buf.append("totalPresentValue").append('=').append(JodaBeanUtils.toString(totalPresentValue)).append(',').append(' ');
    buf.append("totalDelta").append('=').append(JodaBeanUtils.toString(totalDelta)).append(',').append(' ');
    buf.append("bucketedDelta").append('=').append(JodaBeanUtils.toString(bucketedDelta)).append(',').append(' ');
    buf.append("totalGamma").append('=').append(JodaBeanUtils.toString(totalGamma)).append(',').append(' ');
    buf.append("bucketedGamma").append('=').append(JodaBeanUtils.toString(bucketedGamma)).append(',').append(' ');
    buf.append("trades").append('=').append(JodaBeanUtils.toString(trades));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code TradeValuations}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code totalPresentValue} property.
     */
    private final MetaProperty<Double> totalPresentValue = DirectMetaProperty.ofImmutable(
        this, "totalPresentValue", TradeValuations.class, Double.TYPE);
    /**
     * The meta-property for the {@code totalDelta} property.
     */
    private final MetaProperty<Double> totalDelta = DirectMetaProperty.ofImmutable(
        this, "totalDelta", TradeValuations.class, Double.class);
    /**
     * The meta-property for the {@code bucketedDelta} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<TradeCurveSensitivity>> bucketedDelta = DirectMetaProperty.ofImmutable(
        this, "bucketedDelta", TradeValuations.class, (Class) List.class);
    /**
     * The meta-property for the {@code totalGamma} property.
     */
    private final MetaProperty<Double> totalGamma = DirectMetaProperty.ofImmutable(
        this, "totalGamma", TradeValuations.class, Double.class);
    /**
     * The meta-property for the {@code bucketedGamma} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<TradeCurveSensitivity>> bucketedGamma = DirectMetaProperty.ofImmutable(
        this, "bucketedGamma", TradeValuations.class, (Class) List.class);
    /**
     * The meta-property for the {@code trades} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<TradeValuation>> trades = DirectMetaProperty.ofImmutable(
        this, "trades", TradeValuations.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "totalPresentValue",
        "totalDelta",
        "bucketedDelta",
        "totalGamma",
        "bucketedGamma",
        "trades");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2040975066:  // totalPresentValue
          return totalPresentValue;
        case -730768684:  // totalDelta
          return totalDelta;
        case -1611897553:  // bucketedDelta
          return bucketedDelta;
        case -728116541:  // totalGamma
          return totalGamma;
        case -1609245410:  // bucketedGamma
          return bucketedGamma;
        case -865715313:  // trades
          return trades;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends TradeValuations> builder() {
      return new TradeValuations.Builder();
    }

    @Override
    public Class<? extends TradeValuations> beanType() {
      return TradeValuations.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 2040975066:  // totalPresentValue
          return ((TradeValuations) bean).getTotalPresentValue();
        case -730768684:  // totalDelta
          return ((TradeValuations) bean).totalDelta;
        case -1611897553:  // bucketedDelta
          return ((TradeValuations) bean).bucketedDelta;
        case -728116541:  // totalGamma
          return ((TradeValuations) bean).totalGamma;
        case -1609245410:  // bucketedGamma
          return ((TradeValuations) bean).bucketedGamma;
        case -865715313:  // trades
          return ((TradeValuations) bean).getTrades();
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
   * The bean-builder for {@code TradeValuations}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<TradeValuations> {

    private double totalPresentValue;
    private Double totalDelta;
    private List<TradeCurveSensitivity> bucketedDelta;
    private Double totalGamma;
    private List<TradeCurveSensitivity> bucketedGamma;
    private List<TradeValuation> trades = Collections.emptyList();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2040975066:  // totalPresentValue
          return totalPresentValue;
        case -730768684:  // totalDelta
          return totalDelta;
        case -1611897553:  // bucketedDelta
          return bucketedDelta;
        case -728116541:  // totalGamma
          return totalGamma;
        case -1609245410:  // bucketedGamma
          return bucketedGamma;
        case -865715313:  // trades
          return trades;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 2040975066:  // totalPresentValue
          this.totalPresentValue = (Double) newValue;
          break;
        case -730768684:  // totalDelta
          this.totalDelta = (Double) newValue;
          break;
        case -1611897553:  // bucketedDelta
          this.bucketedDelta = (List<TradeCurveSensitivity>) newValue;
          break;
        case -728116541:  // totalGamma
          this.totalGamma = (Double) newValue;
          break;
        case -1609245410:  // bucketedGamma
          this.bucketedGamma = (List<TradeCurveSensitivity>) newValue;
          break;
        case -865715313:  // trades
          this.trades = (List<TradeValuation>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public TradeValuations build() {
      preBuild(this);
      return new TradeValuations(
          totalPresentValue,
          totalDelta,
          bucketedDelta,
          totalGamma,
          bucketedGamma,
          trades);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(224);
      buf.append("TradeValuations.Builder{");
      buf.append("totalPresentValue").append('=').append(JodaBeanUtils.toString(totalPresentValue)).append(',').append(' ');
      buf.append("totalDelta").append('=').append(JodaBeanUtils.toString(totalDelta)).append(',').append(' ');
      buf.append("bucketedDelta").append('=').append(JodaBeanUtils.toString(bucketedDelta)).append(',').append(' ');
      buf.append("totalGamma").append('=').append(JodaBeanUtils.toString(totalGamma)).append(',').append(' ');
      buf.append("bucketedGamma").append('=').append(JodaBeanUtils.toString(bucketedGamma)).append(',').append(' ');
      buf.append("trades").append('=').append(JodaBeanUtils.toString(trades));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
