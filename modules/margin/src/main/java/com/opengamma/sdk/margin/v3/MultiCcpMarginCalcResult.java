/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.v3;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.TypedMetaBean;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.MinimalMetaBean;

@BeanDefinition(constructorScope = "private", metaScope = "private", factoryName = "of", style = "minimal")
public final class MultiCcpMarginCalcResult implements ImmutableBean {
  /**
   * The status of the calculation - PENDING or COMPLETED.
   */
  @PropertyDefinition(validate = "notNull")
  private final MarginCalcResultStatus status;
  /**
   * The type of calculation.
   */
  @PropertyDefinition(validate = "notNull")
  private final MarginCalcRequestType type;
  /**
   * The valuation date for which the portfolio will be processed.
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate valuationDate;
  /**
   * The reporting currency, as an ISO 4217 three letter currency code.
   */
  @PropertyDefinition(validate = "notNull")
  private final String reportingCurrency;

  @PropertyDefinition(validate = "notNull")
  private final boolean applyClientMultiplier;
  /**
   * The summary of the portfolio items, may be empty.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<PortfolioItemSummary> portfolioItems;

  /**
   * The result of the margin calculation, grouped by CCP
   */
  @PropertyDefinition(get = "optional")
  private final Map<Ccp, MarginSummary> margin;

  /**
   * The list of failures that occurred, may be empty.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<MarginError> failures;

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code MultiCcpMarginCalcResult}.
   */
  private static final TypedMetaBean<MultiCcpMarginCalcResult> META_BEAN =
      MinimalMetaBean.of(
          MultiCcpMarginCalcResult.class,
          () -> new MultiCcpMarginCalcResult.Builder(),
          b -> b.getStatus(),
          b -> b.getType(),
          b -> b.getValuationDate(),
          b -> b.getReportingCurrency(),
          b -> b.isApplyClientMultiplier(),
          b -> b.getPortfolioItems(),
          b -> b.margin,
          b -> b.getFailures());

  /**
   * The meta-bean for {@code MultiCcpMarginCalcResult}.
   * @return the meta-bean, not null
   */
  public static TypedMetaBean<MultiCcpMarginCalcResult> meta() {
    return META_BEAN;
  }

  static {
    MetaBean.register(META_BEAN);
  }

  /**
   * Obtains an instance.
   * @param status  the value of the property, not null
   * @param type  the value of the property, not null
   * @param valuationDate  the value of the property, not null
   * @param reportingCurrency  the value of the property, not null
   * @param applyClientMultiplier  the value of the property, not null
   * @param portfolioItems  the value of the property, not null
   * @param margin  the value of the property
   * @param failures  the value of the property, not null
   * @return the instance
   */
  public static MultiCcpMarginCalcResult of(
      MarginCalcResultStatus status,
      MarginCalcRequestType type,
      LocalDate valuationDate,
      String reportingCurrency,
      boolean applyClientMultiplier,
      List<PortfolioItemSummary> portfolioItems,
      Map<Ccp, MarginSummary> margin,
      List<MarginError> failures) {
    return new MultiCcpMarginCalcResult(
      status,
      type,
      valuationDate,
      reportingCurrency,
      applyClientMultiplier,
      portfolioItems,
      margin,
      failures);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static MultiCcpMarginCalcResult.Builder builder() {
    return new MultiCcpMarginCalcResult.Builder();
  }

  private MultiCcpMarginCalcResult(
      MarginCalcResultStatus status,
      MarginCalcRequestType type,
      LocalDate valuationDate,
      String reportingCurrency,
      boolean applyClientMultiplier,
      List<PortfolioItemSummary> portfolioItems,
      Map<Ccp, MarginSummary> margin,
      List<MarginError> failures) {
    JodaBeanUtils.notNull(status, "status");
    JodaBeanUtils.notNull(type, "type");
    JodaBeanUtils.notNull(valuationDate, "valuationDate");
    JodaBeanUtils.notNull(reportingCurrency, "reportingCurrency");
    JodaBeanUtils.notNull(applyClientMultiplier, "applyClientMultiplier");
    JodaBeanUtils.notNull(portfolioItems, "portfolioItems");
    JodaBeanUtils.notNull(failures, "failures");
    this.status = status;
    this.type = type;
    this.valuationDate = valuationDate;
    this.reportingCurrency = reportingCurrency;
    this.applyClientMultiplier = applyClientMultiplier;
    this.portfolioItems = Collections.unmodifiableList(new ArrayList<>(portfolioItems));
    this.margin = (margin != null ? Collections.unmodifiableMap(new HashMap<>(margin)) : null);
    this.failures = Collections.unmodifiableList(new ArrayList<>(failures));
  }

  @Override
  public TypedMetaBean<MultiCcpMarginCalcResult> metaBean() {
    return META_BEAN;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the status of the calculation - PENDING or COMPLETED.
   * @return the value of the property, not null
   */
  public MarginCalcResultStatus getStatus() {
    return status;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the type of calculation.
   * @return the value of the property, not null
   */
  public MarginCalcRequestType getType() {
    return type;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valuation date for which the portfolio will be processed.
   * @return the value of the property, not null
   */
  public LocalDate getValuationDate() {
    return valuationDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the reporting currency, as an ISO 4217 three letter currency code.
   * @return the value of the property, not null
   */
  public String getReportingCurrency() {
    return reportingCurrency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the applyClientMultiplier.
   * @return the value of the property, not null
   */
  public boolean isApplyClientMultiplier() {
    return applyClientMultiplier;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the summary of the portfolio items, may be empty.
   * @return the value of the property, not null
   */
  public List<PortfolioItemSummary> getPortfolioItems() {
    return portfolioItems;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the result of the margin calculation, grouped by CCP
   * @return the optional value of the property, not null
   */
  public Optional<Map<Ccp, MarginSummary>> getMargin() {
    return Optional.ofNullable(margin);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the list of failures that occurred, may be empty.
   * @return the value of the property, not null
   */
  public List<MarginError> getFailures() {
    return failures;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      MultiCcpMarginCalcResult other = (MultiCcpMarginCalcResult) obj;
      return JodaBeanUtils.equal(status, other.status) &&
          JodaBeanUtils.equal(type, other.type) &&
          JodaBeanUtils.equal(valuationDate, other.valuationDate) &&
          JodaBeanUtils.equal(reportingCurrency, other.reportingCurrency) &&
          (applyClientMultiplier == other.applyClientMultiplier) &&
          JodaBeanUtils.equal(portfolioItems, other.portfolioItems) &&
          JodaBeanUtils.equal(margin, other.margin) &&
          JodaBeanUtils.equal(failures, other.failures);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(status);
    hash = hash * 31 + JodaBeanUtils.hashCode(type);
    hash = hash * 31 + JodaBeanUtils.hashCode(valuationDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(reportingCurrency);
    hash = hash * 31 + JodaBeanUtils.hashCode(applyClientMultiplier);
    hash = hash * 31 + JodaBeanUtils.hashCode(portfolioItems);
    hash = hash * 31 + JodaBeanUtils.hashCode(margin);
    hash = hash * 31 + JodaBeanUtils.hashCode(failures);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(288);
    buf.append("MultiCcpMarginCalcResult{");
    buf.append("status").append('=').append(status).append(',').append(' ');
    buf.append("type").append('=').append(type).append(',').append(' ');
    buf.append("valuationDate").append('=').append(valuationDate).append(',').append(' ');
    buf.append("reportingCurrency").append('=').append(reportingCurrency).append(',').append(' ');
    buf.append("applyClientMultiplier").append('=').append(applyClientMultiplier).append(',').append(' ');
    buf.append("portfolioItems").append('=').append(portfolioItems).append(',').append(' ');
    buf.append("margin").append('=').append(margin).append(',').append(' ');
    buf.append("failures").append('=').append(JodaBeanUtils.toString(failures));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code MultiCcpMarginCalcResult}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<MultiCcpMarginCalcResult> {

    private MarginCalcResultStatus status;
    private MarginCalcRequestType type;
    private LocalDate valuationDate;
    private String reportingCurrency;
    private boolean applyClientMultiplier;
    private List<PortfolioItemSummary> portfolioItems = Collections.emptyList();
    private Map<Ccp, MarginSummary> margin;
    private List<MarginError> failures = Collections.emptyList();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(MultiCcpMarginCalcResult beanToCopy) {
      this.status = beanToCopy.getStatus();
      this.type = beanToCopy.getType();
      this.valuationDate = beanToCopy.getValuationDate();
      this.reportingCurrency = beanToCopy.getReportingCurrency();
      this.applyClientMultiplier = beanToCopy.isApplyClientMultiplier();
      this.portfolioItems = new ArrayList<>(beanToCopy.getPortfolioItems());
      this.margin = (beanToCopy.margin != null ? new HashMap<>(beanToCopy.margin) : null);
      this.failures = new ArrayList<>(beanToCopy.getFailures());
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -892481550:  // status
          return status;
        case 3575610:  // type
          return type;
        case 113107279:  // valuationDate
          return valuationDate;
        case -1287844769:  // reportingCurrency
          return reportingCurrency;
        case 1555658618:  // applyClientMultiplier
          return applyClientMultiplier;
        case 110493528:  // portfolioItems
          return portfolioItems;
        case -1081309778:  // margin
          return margin;
        case 675938345:  // failures
          return failures;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -892481550:  // status
          this.status = (MarginCalcResultStatus) newValue;
          break;
        case 3575610:  // type
          this.type = (MarginCalcRequestType) newValue;
          break;
        case 113107279:  // valuationDate
          this.valuationDate = (LocalDate) newValue;
          break;
        case -1287844769:  // reportingCurrency
          this.reportingCurrency = (String) newValue;
          break;
        case 1555658618:  // applyClientMultiplier
          this.applyClientMultiplier = (Boolean) newValue;
          break;
        case 110493528:  // portfolioItems
          this.portfolioItems = (List<PortfolioItemSummary>) newValue;
          break;
        case -1081309778:  // margin
          this.margin = (Map<Ccp, MarginSummary>) newValue;
          break;
        case 675938345:  // failures
          this.failures = (List<MarginError>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public MultiCcpMarginCalcResult build() {
      return new MultiCcpMarginCalcResult(
          status,
          type,
          valuationDate,
          reportingCurrency,
          applyClientMultiplier,
          portfolioItems,
          margin,
          failures);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the status of the calculation - PENDING or COMPLETED.
     * @param status  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder status(MarginCalcResultStatus status) {
      JodaBeanUtils.notNull(status, "status");
      this.status = status;
      return this;
    }

    /**
     * Sets the type of calculation.
     * @param type  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder type(MarginCalcRequestType type) {
      JodaBeanUtils.notNull(type, "type");
      this.type = type;
      return this;
    }

    /**
     * Sets the valuation date for which the portfolio will be processed.
     * @param valuationDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder valuationDate(LocalDate valuationDate) {
      JodaBeanUtils.notNull(valuationDate, "valuationDate");
      this.valuationDate = valuationDate;
      return this;
    }

    /**
     * Sets the reporting currency, as an ISO 4217 three letter currency code.
     * @param reportingCurrency  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder reportingCurrency(String reportingCurrency) {
      JodaBeanUtils.notNull(reportingCurrency, "reportingCurrency");
      this.reportingCurrency = reportingCurrency;
      return this;
    }

    /**
     * Sets the applyClientMultiplier.
     * @param applyClientMultiplier  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder applyClientMultiplier(boolean applyClientMultiplier) {
      JodaBeanUtils.notNull(applyClientMultiplier, "applyClientMultiplier");
      this.applyClientMultiplier = applyClientMultiplier;
      return this;
    }

    /**
     * Sets the summary of the portfolio items, may be empty.
     * @param portfolioItems  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder portfolioItems(List<PortfolioItemSummary> portfolioItems) {
      JodaBeanUtils.notNull(portfolioItems, "portfolioItems");
      this.portfolioItems = portfolioItems;
      return this;
    }

    /**
     * Sets the {@code portfolioItems} property in the builder
     * from an array of objects.
     * @param portfolioItems  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder portfolioItems(PortfolioItemSummary... portfolioItems) {
      return portfolioItems(Arrays.asList(portfolioItems));
    }

    /**
     * Sets the result of the margin calculation, grouped by CCP
     * @param margin  the new value
     * @return this, for chaining, not null
     */
    public Builder margin(Map<Ccp, MarginSummary> margin) {
      this.margin = margin;
      return this;
    }

    /**
     * Sets the list of failures that occurred, may be empty.
     * @param failures  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder failures(List<MarginError> failures) {
      JodaBeanUtils.notNull(failures, "failures");
      this.failures = failures;
      return this;
    }

    /**
     * Sets the {@code failures} property in the builder
     * from an array of objects.
     * @param failures  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder failures(MarginError... failures) {
      return failures(Arrays.asList(failures));
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(288);
      buf.append("MultiCcpMarginCalcResult.Builder{");
      buf.append("status").append('=').append(JodaBeanUtils.toString(status)).append(',').append(' ');
      buf.append("type").append('=').append(JodaBeanUtils.toString(type)).append(',').append(' ');
      buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(valuationDate)).append(',').append(' ');
      buf.append("reportingCurrency").append('=').append(JodaBeanUtils.toString(reportingCurrency)).append(',').append(' ');
      buf.append("applyClientMultiplier").append('=').append(JodaBeanUtils.toString(applyClientMultiplier)).append(',').append(' ');
      buf.append("portfolioItems").append('=').append(JodaBeanUtils.toString(portfolioItems)).append(',').append(' ');
      buf.append("margin").append('=').append(JodaBeanUtils.toString(margin)).append(',').append(' ');
      buf.append("failures").append('=').append(JodaBeanUtils.toString(failures));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
