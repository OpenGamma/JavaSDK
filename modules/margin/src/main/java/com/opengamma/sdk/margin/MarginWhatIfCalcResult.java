/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

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
 * Result from the service.
 */
@BeanDefinition(builderScope = "private", metaScope = "private")
public final class MarginWhatIfCalcResult implements ImmutableBean {

  /**
   * The status of the calculation - PENDING or COMPLETED.
   */
  @PropertyDefinition(validate = "notNull")
  private final MarginCalcResultStatus status;
  /**
   * The types of calculation that were performed.
   * <p>
   * The types may differ from the requested calculation types if the user does not have
   * the necessary permission for the calculation, or the CCP does not support it.
   */
  @PropertyDefinition(validate = "notNull")
  private final Set<MarginCalcType> calculationTypes;
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
  /**
   * The summary of the portfolio items, may be empty.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<PortfolioItemSummary> portfolioItems;
  /**
   * The result of the margin calculation for the base portfolio.
   */
  @PropertyDefinition
  private final MarginSummary baseSummary;
  /**
   * The result of the margin calculation for the combined portfolio (base + delta).
   */
  @PropertyDefinition
  private final MarginSummary combinedSummary;
  /**
   * The difference between the base and combined portfolio.
   * This is the change of margin that would occur if the delta portfolio was addded.
   */
  @PropertyDefinition
  private final MarginSummary deltaSummary;
  /**
   * The list of failures that occurred, may be empty.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<MarginError> failures;

  //-------------------------------------------------------------------------
  /**
   * Creates a new instance of {@link MarginWhatIfCalcResult} with the given details.
   *
   * @param status  the request status, as an instance of {@link MarginCalcResultStatus}
   * @param types  the types of calculation that were performed
   * @param valuationDate  the valuation date for which the portfolio will be processed
   * @param reportingCurrency  the reporting currency, as an ISO 4217 three letter currency code
   * @param portfolioItems  the summary of the portfolio items, may be empty
   * @param baseSummary  the details of the base portfolio margin calculation
   * @param combinedSummary  the details of the combined (base + delta) portfolios margin calculation
   * @param failures  the list of failures that occurred, may be empty
   * @return a new instance of {@link MarginWhatIfCalcResult}
   */
  public static MarginWhatIfCalcResult of(
      MarginCalcResultStatus status,
      Set<MarginCalcType> types,
      LocalDate valuationDate,
      String reportingCurrency,
      List<PortfolioItemSummary> portfolioItems,
      MarginSummary baseSummary,
      MarginSummary combinedSummary,
      List<MarginError> failures) {

    Map<String, Double> baseSummaryDetails = baseSummary.getMarginDetails().stream()
        .collect(Collectors.toMap(NamedValue::getKey, NamedValue::getValue));
    Map<String, Double> combinedSummaryDetails = combinedSummary.getMarginDetails().stream()
        .collect(Collectors.toMap(NamedValue::getKey, NamedValue::getValue));
    List<NamedValue> deltaDetails = baseSummaryDetails.entrySet().stream()
        .map(namedValue -> NamedValue.of(
            namedValue.getKey(), combinedSummaryDetails.get(namedValue.getKey()) - namedValue.getValue()))
        .collect(Collectors.toList());
    MarginBreakdown baseBrk = baseSummary.getBreakdown();
    MarginBreakdown combinedBrk = combinedSummary.getBreakdown();
    MarginBreakdown diffBreakdown = MarginBreakdown.of(
        combinedBrk.getTotalMargin() - baseBrk.getTotalMargin(),
        combinedBrk.getBaseMargin() - baseBrk.getBaseMargin(),
        combinedBrk.getAddOns() - baseBrk.getAddOns(),
        combinedBrk.getNetLiquidatingValue() - baseBrk.getNetLiquidatingValue());

    double marginDifference = combinedSummary.getMargin() - baseSummary.getMargin();
    MarginSummary deltaSummary = MarginSummary.of(marginDifference, deltaDetails, diffBreakdown);

    return new MarginWhatIfCalcResult(
        status,
        types,
        valuationDate,
        reportingCurrency,
        portfolioItems,
        baseSummary,
        combinedSummary,
        deltaSummary,
        failures);
  }

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param status  the request status, as an instance of {@link MarginCalcResultStatus}
   * @param type  the type of request, as an instance of {@link MarginCalcRequestType}
   * @param valuationDate  the valuation date for which the portfolio will be processed
   * @param reportingCurrency  the reporting currency, as an ISO 4217 three letter currency code
   * @param portfolioItems  the summary of the portfolio items, may be empty
   * @param baseSummary  the details of the base portfolio margin calculation
   * @param combinedSummary  the details of the combined (base + delta) portfolios margin calculation
   * @param deltaSummary  the details of the delta portfolio margin calculation
   * @param failures  the list of failures that occurred, may be empty
   * @return the instance
   * @deprecated Use the version of this method taking {@code MarginCalcType}
   *   (it is intended that the SDK creates instances of this class, your code should only create instances in tests)
   */
  @Deprecated
  public static MarginWhatIfCalcResult of(
      MarginCalcResultStatus status,
      MarginCalcRequestType type,
      LocalDate valuationDate,
      String reportingCurrency,
      List<PortfolioItemSummary> portfolioItems,
      MarginSummary baseSummary,
      MarginSummary combinedSummary,
      MarginSummary deltaSummary,
      List<MarginError> failures) {

    return new MarginWhatIfCalcResult(
        status,
        type.toCalculationTypes(),
        valuationDate,
        reportingCurrency,
        portfolioItems,
        baseSummary,
        combinedSummary,
        deltaSummary,
        failures);
  }

  /**
   * Creates a new instance of {@link MarginWhatIfCalcResult} with the given details.
   *
   * @param status  the request status, as an instance of {@link MarginCalcResultStatus}
   * @param type  the type of request, as an instance of {@link MarginCalcRequestType}
   * @param valuationDate  the valuation date for which the portfolio will be processed
   * @param reportingCurrency  the reporting currency, as an ISO 4217 three letter currency code
   * @param portfolioItems  the summary of the portfolio items, may be empty
   * @param baseSummary  the details of the base portfolio margin calculation
   * @param combinedSummary  the details of the combined (base + delta) portfolios margin calculation
   * @param failures  the list of failures that occurred, may be empty
   * @return a new instance of {@link MarginWhatIfCalcResult}
   * @deprecated Use the version of this method taking {@code MarginCalcType}
   *   (it is intended that the SDK creates instances of this class, your code should only create instances in tests)
   */
  @Deprecated
  public static MarginWhatIfCalcResult of(
      MarginCalcResultStatus status,
      MarginCalcRequestType type,
      LocalDate valuationDate,
      String reportingCurrency,
      List<PortfolioItemSummary> portfolioItems,
      MarginSummary baseSummary,
      MarginSummary combinedSummary,
      List<MarginError> failures) {

    return of(
        status,
        type.toCalculationTypes(),
        valuationDate,
        reportingCurrency,
        portfolioItems,
        baseSummary,
        combinedSummary,
        failures);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the type of the calculation request.
   * 
   * @return the type
   * @deprecated Use {@link #getCalculationTypes()} instead
   */
  @Deprecated
  public MarginCalcRequestType getType() {
    // this is provided to retain backwards compatibility of the REST API
    // if the types contains neither MARGIN nor PORTFOLIO_SUMMARY then the caller
    // must be using the newer form of the API, but we have to return a value to avoid NPE
    boolean margin = calculationTypes.contains(MarginCalcType.MARGIN);
    boolean summary = calculationTypes.contains(MarginCalcType.PORTFOLIO_SUMMARY);
    if (margin) {
      return summary ? MarginCalcRequestType.FULL : MarginCalcRequestType.STANDARD;
    } else {
      return summary ? MarginCalcRequestType.PARSE_INPUTS : MarginCalcRequestType.STANDARD;
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code MarginWhatIfCalcResult}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return MarginWhatIfCalcResult.Meta.INSTANCE;
  }

  static {
    MetaBean.register(MarginWhatIfCalcResult.Meta.INSTANCE);
  }

  private MarginWhatIfCalcResult(
      MarginCalcResultStatus status,
      Set<MarginCalcType> calculationTypes,
      LocalDate valuationDate,
      String reportingCurrency,
      List<PortfolioItemSummary> portfolioItems,
      MarginSummary baseSummary,
      MarginSummary combinedSummary,
      MarginSummary deltaSummary,
      List<MarginError> failures) {
    JodaBeanUtils.notNull(status, "status");
    JodaBeanUtils.notNull(calculationTypes, "calculationTypes");
    JodaBeanUtils.notNull(valuationDate, "valuationDate");
    JodaBeanUtils.notNull(reportingCurrency, "reportingCurrency");
    JodaBeanUtils.notNull(portfolioItems, "portfolioItems");
    JodaBeanUtils.notNull(failures, "failures");
    this.status = status;
    this.calculationTypes = Collections.unmodifiableSet(new HashSet<>(calculationTypes));
    this.valuationDate = valuationDate;
    this.reportingCurrency = reportingCurrency;
    this.portfolioItems = Collections.unmodifiableList(new ArrayList<>(portfolioItems));
    this.baseSummary = baseSummary;
    this.combinedSummary = combinedSummary;
    this.deltaSummary = deltaSummary;
    this.failures = Collections.unmodifiableList(new ArrayList<>(failures));
  }

  @Override
  public MetaBean metaBean() {
    return MarginWhatIfCalcResult.Meta.INSTANCE;
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
   * Gets the types of calculation that were performed.
   * <p>
   * The types may differ from the requested calculation types if the user does not have
   * the necessary permission for the calculation, or the CCP does not support it.
   * @return the value of the property, not null
   */
  public Set<MarginCalcType> getCalculationTypes() {
    return calculationTypes;
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
   * Gets the summary of the portfolio items, may be empty.
   * @return the value of the property, not null
   */
  public List<PortfolioItemSummary> getPortfolioItems() {
    return portfolioItems;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the result of the margin calculation for the base portfolio.
   * @return the value of the property
   */
  public MarginSummary getBaseSummary() {
    return baseSummary;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the result of the margin calculation for the combined portfolio (base + delta).
   * @return the value of the property
   */
  public MarginSummary getCombinedSummary() {
    return combinedSummary;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the difference between the base and combined portfolio.
   * This is the change of margin that would occur if the delta portfolio was addded.
   * @return the value of the property
   */
  public MarginSummary getDeltaSummary() {
    return deltaSummary;
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
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      MarginWhatIfCalcResult other = (MarginWhatIfCalcResult) obj;
      return JodaBeanUtils.equal(status, other.status) &&
          JodaBeanUtils.equal(calculationTypes, other.calculationTypes) &&
          JodaBeanUtils.equal(valuationDate, other.valuationDate) &&
          JodaBeanUtils.equal(reportingCurrency, other.reportingCurrency) &&
          JodaBeanUtils.equal(portfolioItems, other.portfolioItems) &&
          JodaBeanUtils.equal(baseSummary, other.baseSummary) &&
          JodaBeanUtils.equal(combinedSummary, other.combinedSummary) &&
          JodaBeanUtils.equal(deltaSummary, other.deltaSummary) &&
          JodaBeanUtils.equal(failures, other.failures);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(status);
    hash = hash * 31 + JodaBeanUtils.hashCode(calculationTypes);
    hash = hash * 31 + JodaBeanUtils.hashCode(valuationDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(reportingCurrency);
    hash = hash * 31 + JodaBeanUtils.hashCode(portfolioItems);
    hash = hash * 31 + JodaBeanUtils.hashCode(baseSummary);
    hash = hash * 31 + JodaBeanUtils.hashCode(combinedSummary);
    hash = hash * 31 + JodaBeanUtils.hashCode(deltaSummary);
    hash = hash * 31 + JodaBeanUtils.hashCode(failures);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(320);
    buf.append("MarginWhatIfCalcResult{");
    buf.append("status").append('=').append(JodaBeanUtils.toString(status)).append(',').append(' ');
    buf.append("calculationTypes").append('=').append(JodaBeanUtils.toString(calculationTypes)).append(',').append(' ');
    buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(valuationDate)).append(',').append(' ');
    buf.append("reportingCurrency").append('=').append(JodaBeanUtils.toString(reportingCurrency)).append(',').append(' ');
    buf.append("portfolioItems").append('=').append(JodaBeanUtils.toString(portfolioItems)).append(',').append(' ');
    buf.append("baseSummary").append('=').append(JodaBeanUtils.toString(baseSummary)).append(',').append(' ');
    buf.append("combinedSummary").append('=').append(JodaBeanUtils.toString(combinedSummary)).append(',').append(' ');
    buf.append("deltaSummary").append('=').append(JodaBeanUtils.toString(deltaSummary)).append(',').append(' ');
    buf.append("failures").append('=').append(JodaBeanUtils.toString(failures));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code MarginWhatIfCalcResult}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code status} property.
     */
    private final MetaProperty<MarginCalcResultStatus> status = DirectMetaProperty.ofImmutable(
        this, "status", MarginWhatIfCalcResult.class, MarginCalcResultStatus.class);
    /**
     * The meta-property for the {@code calculationTypes} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<MarginCalcType>> calculationTypes = DirectMetaProperty.ofImmutable(
        this, "calculationTypes", MarginWhatIfCalcResult.class, (Class) Set.class);
    /**
     * The meta-property for the {@code valuationDate} property.
     */
    private final MetaProperty<LocalDate> valuationDate = DirectMetaProperty.ofImmutable(
        this, "valuationDate", MarginWhatIfCalcResult.class, LocalDate.class);
    /**
     * The meta-property for the {@code reportingCurrency} property.
     */
    private final MetaProperty<String> reportingCurrency = DirectMetaProperty.ofImmutable(
        this, "reportingCurrency", MarginWhatIfCalcResult.class, String.class);
    /**
     * The meta-property for the {@code portfolioItems} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<PortfolioItemSummary>> portfolioItems = DirectMetaProperty.ofImmutable(
        this, "portfolioItems", MarginWhatIfCalcResult.class, (Class) List.class);
    /**
     * The meta-property for the {@code baseSummary} property.
     */
    private final MetaProperty<MarginSummary> baseSummary = DirectMetaProperty.ofImmutable(
        this, "baseSummary", MarginWhatIfCalcResult.class, MarginSummary.class);
    /**
     * The meta-property for the {@code combinedSummary} property.
     */
    private final MetaProperty<MarginSummary> combinedSummary = DirectMetaProperty.ofImmutable(
        this, "combinedSummary", MarginWhatIfCalcResult.class, MarginSummary.class);
    /**
     * The meta-property for the {@code deltaSummary} property.
     */
    private final MetaProperty<MarginSummary> deltaSummary = DirectMetaProperty.ofImmutable(
        this, "deltaSummary", MarginWhatIfCalcResult.class, MarginSummary.class);
    /**
     * The meta-property for the {@code failures} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<MarginError>> failures = DirectMetaProperty.ofImmutable(
        this, "failures", MarginWhatIfCalcResult.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "status",
        "calculationTypes",
        "valuationDate",
        "reportingCurrency",
        "portfolioItems",
        "baseSummary",
        "combinedSummary",
        "deltaSummary",
        "failures");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -892481550:  // status
          return status;
        case 755457840:  // calculationTypes
          return calculationTypes;
        case 113107279:  // valuationDate
          return valuationDate;
        case -1287844769:  // reportingCurrency
          return reportingCurrency;
        case 110493528:  // portfolioItems
          return portfolioItems;
        case 443816853:  // baseSummary
          return baseSummary;
        case 1067455713:  // combinedSummary
          return combinedSummary;
        case 1315857614:  // deltaSummary
          return deltaSummary;
        case 675938345:  // failures
          return failures;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends MarginWhatIfCalcResult> builder() {
      return new MarginWhatIfCalcResult.Builder();
    }

    @Override
    public Class<? extends MarginWhatIfCalcResult> beanType() {
      return MarginWhatIfCalcResult.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -892481550:  // status
          return ((MarginWhatIfCalcResult) bean).getStatus();
        case 755457840:  // calculationTypes
          return ((MarginWhatIfCalcResult) bean).getCalculationTypes();
        case 113107279:  // valuationDate
          return ((MarginWhatIfCalcResult) bean).getValuationDate();
        case -1287844769:  // reportingCurrency
          return ((MarginWhatIfCalcResult) bean).getReportingCurrency();
        case 110493528:  // portfolioItems
          return ((MarginWhatIfCalcResult) bean).getPortfolioItems();
        case 443816853:  // baseSummary
          return ((MarginWhatIfCalcResult) bean).getBaseSummary();
        case 1067455713:  // combinedSummary
          return ((MarginWhatIfCalcResult) bean).getCombinedSummary();
        case 1315857614:  // deltaSummary
          return ((MarginWhatIfCalcResult) bean).getDeltaSummary();
        case 675938345:  // failures
          return ((MarginWhatIfCalcResult) bean).getFailures();
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
   * The bean-builder for {@code MarginWhatIfCalcResult}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<MarginWhatIfCalcResult> {

    private MarginCalcResultStatus status;
    private Set<MarginCalcType> calculationTypes = Collections.emptySet();
    private LocalDate valuationDate;
    private String reportingCurrency;
    private List<PortfolioItemSummary> portfolioItems = Collections.emptyList();
    private MarginSummary baseSummary;
    private MarginSummary combinedSummary;
    private MarginSummary deltaSummary;
    private List<MarginError> failures = Collections.emptyList();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -892481550:  // status
          return status;
        case 755457840:  // calculationTypes
          return calculationTypes;
        case 113107279:  // valuationDate
          return valuationDate;
        case -1287844769:  // reportingCurrency
          return reportingCurrency;
        case 110493528:  // portfolioItems
          return portfolioItems;
        case 443816853:  // baseSummary
          return baseSummary;
        case 1067455713:  // combinedSummary
          return combinedSummary;
        case 1315857614:  // deltaSummary
          return deltaSummary;
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
        case 755457840:  // calculationTypes
          this.calculationTypes = (Set<MarginCalcType>) newValue;
          break;
        case 113107279:  // valuationDate
          this.valuationDate = (LocalDate) newValue;
          break;
        case -1287844769:  // reportingCurrency
          this.reportingCurrency = (String) newValue;
          break;
        case 110493528:  // portfolioItems
          this.portfolioItems = (List<PortfolioItemSummary>) newValue;
          break;
        case 443816853:  // baseSummary
          this.baseSummary = (MarginSummary) newValue;
          break;
        case 1067455713:  // combinedSummary
          this.combinedSummary = (MarginSummary) newValue;
          break;
        case 1315857614:  // deltaSummary
          this.deltaSummary = (MarginSummary) newValue;
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
    public MarginWhatIfCalcResult build() {
      return new MarginWhatIfCalcResult(
          status,
          calculationTypes,
          valuationDate,
          reportingCurrency,
          portfolioItems,
          baseSummary,
          combinedSummary,
          deltaSummary,
          failures);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(320);
      buf.append("MarginWhatIfCalcResult.Builder{");
      buf.append("status").append('=').append(JodaBeanUtils.toString(status)).append(',').append(' ');
      buf.append("calculationTypes").append('=').append(JodaBeanUtils.toString(calculationTypes)).append(',').append(' ');
      buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(valuationDate)).append(',').append(' ');
      buf.append("reportingCurrency").append('=').append(JodaBeanUtils.toString(reportingCurrency)).append(',').append(' ');
      buf.append("portfolioItems").append('=').append(JodaBeanUtils.toString(portfolioItems)).append(',').append(' ');
      buf.append("baseSummary").append('=').append(JodaBeanUtils.toString(baseSummary)).append(',').append(' ');
      buf.append("combinedSummary").append('=').append(JodaBeanUtils.toString(combinedSummary)).append(',').append(' ');
      buf.append("deltaSummary").append('=').append(JodaBeanUtils.toString(deltaSummary)).append(',').append(' ');
      buf.append("failures").append('=').append(JodaBeanUtils.toString(failures));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
