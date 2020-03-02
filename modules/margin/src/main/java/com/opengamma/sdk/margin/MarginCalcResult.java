/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
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
import java.util.Optional;
import java.util.Set;

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
@BeanDefinition(builderScope = "private", metaScope = "private", factoryName = "of")
public final class MarginCalcResult implements ImmutableBean {

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
   * The mode of the calculation - SPOT or FORWARD.
   */
  @PropertyDefinition(validate = "notNull")
  private final MarginCalcMode mode;
  /**
   * The valuation date of the calculation.
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate valuationDate;
  /**
   * The reporting currency, as an ISO 4217 three letter currency code.
   */
  @PropertyDefinition(validate = "notNull")
  private final String reportingCurrency;
  /**
   * The currency that the calculation was performed in, as an ISO 4217 three letter currency code.
   */
  @PropertyDefinition(validate = "notNull")
  private final String calculationCurrency;
  /**
   * Whether to apply the client multiplier.
   */
  @PropertyDefinition(validate = "notNull")
  private final boolean applyClientMultiplier;
  /**
   * The summary of the portfolio items, may be empty.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<PortfolioItemSummary> portfolioItems;
  /**
   * The result of the margin calculation
   */
  @PropertyDefinition(get = "optional")
  private final MarginSummary margin;
  /**
   * The detailed breakdown of the margin calculation.
   * The structure varies by CCP.
   */
  @PropertyDefinition(get = "optional")
  private final MarginDetail marginDetail;
  /**
   * The valuation of the trades, including present value, delta and gamma results.
   * Sensitivity-based inputs are not valued.
   */
  @PropertyDefinition(get = "optional")
  private final TradeValuations tradeValuations;
  /**
   * The list of failures that occurred, may be empty.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<MarginError> failures;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * @param status  the value of the property, not null
   * @param type  the value of the property, not null
   * @param mode  the value of the property, not null
   * @param valuationDate  the value of the property, not null
   * @param reportingCurrency  the value of the property, not null
   * @param applyClientMultiplier  the value of the property, not null
   * @param portfolioItems  the value of the property, not null
   * @param margin  the value of the property
   * @param failures  the value of the property, not null
   * @return the instance
   * @deprecated Use the 12-arg version of this method
   *   (it is intended that the SDK creates instances of this class, your code should only create instances in tests)
   */
  @Deprecated
  public static MarginCalcResult of(
      MarginCalcResultStatus status,
      MarginCalcRequestType type,
      MarginCalcMode mode,
      LocalDate valuationDate,
      String reportingCurrency,
      boolean applyClientMultiplier,
      List<PortfolioItemSummary> portfolioItems,
      MarginSummary margin,
      List<MarginError> failures) {

    return new MarginCalcResult(
        status,
        type.toCalculationTypes(),
        mode,
        valuationDate,
        reportingCurrency,
        reportingCurrency,
        applyClientMultiplier,
        portfolioItems,
        margin,
        null,
        null,
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
    // this is provided to retain backwards compatibility of the client library
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
   * The meta-bean for {@code MarginCalcResult}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return MarginCalcResult.Meta.INSTANCE;
  }

  static {
    MetaBean.register(MarginCalcResult.Meta.INSTANCE);
  }

  /**
   * Obtains an instance.
   * @param status  the value of the property, not null
   * @param calculationTypes  the value of the property, not null
   * @param mode  the value of the property, not null
   * @param valuationDate  the value of the property, not null
   * @param reportingCurrency  the value of the property, not null
   * @param calculationCurrency  the value of the property, not null
   * @param applyClientMultiplier  the value of the property, not null
   * @param portfolioItems  the value of the property, not null
   * @param margin  the value of the property
   * @param marginDetail  the value of the property
   * @param tradeValuations  the value of the property
   * @param failures  the value of the property, not null
   * @return the instance
   */
  public static MarginCalcResult of(
      MarginCalcResultStatus status,
      Set<MarginCalcType> calculationTypes,
      MarginCalcMode mode,
      LocalDate valuationDate,
      String reportingCurrency,
      String calculationCurrency,
      boolean applyClientMultiplier,
      List<PortfolioItemSummary> portfolioItems,
      MarginSummary margin,
      MarginDetail marginDetail,
      TradeValuations tradeValuations,
      List<MarginError> failures) {
    return new MarginCalcResult(
      status,
      calculationTypes,
      mode,
      valuationDate,
      reportingCurrency,
      calculationCurrency,
      applyClientMultiplier,
      portfolioItems,
      margin,
      marginDetail,
      tradeValuations,
      failures);
  }

  private MarginCalcResult(
      MarginCalcResultStatus status,
      Set<MarginCalcType> calculationTypes,
      MarginCalcMode mode,
      LocalDate valuationDate,
      String reportingCurrency,
      String calculationCurrency,
      boolean applyClientMultiplier,
      List<PortfolioItemSummary> portfolioItems,
      MarginSummary margin,
      MarginDetail marginDetail,
      TradeValuations tradeValuations,
      List<MarginError> failures) {
    JodaBeanUtils.notNull(status, "status");
    JodaBeanUtils.notNull(calculationTypes, "calculationTypes");
    JodaBeanUtils.notNull(mode, "mode");
    JodaBeanUtils.notNull(valuationDate, "valuationDate");
    JodaBeanUtils.notNull(reportingCurrency, "reportingCurrency");
    JodaBeanUtils.notNull(calculationCurrency, "calculationCurrency");
    JodaBeanUtils.notNull(applyClientMultiplier, "applyClientMultiplier");
    JodaBeanUtils.notNull(portfolioItems, "portfolioItems");
    JodaBeanUtils.notNull(failures, "failures");
    this.status = status;
    this.calculationTypes = Collections.unmodifiableSet(new HashSet<>(calculationTypes));
    this.mode = mode;
    this.valuationDate = valuationDate;
    this.reportingCurrency = reportingCurrency;
    this.calculationCurrency = calculationCurrency;
    this.applyClientMultiplier = applyClientMultiplier;
    this.portfolioItems = Collections.unmodifiableList(new ArrayList<>(portfolioItems));
    this.margin = margin;
    this.marginDetail = marginDetail;
    this.tradeValuations = tradeValuations;
    this.failures = Collections.unmodifiableList(new ArrayList<>(failures));
  }

  @Override
  public MetaBean metaBean() {
    return MarginCalcResult.Meta.INSTANCE;
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
   * Gets the mode of the calculation - SPOT or FORWARD.
   * @return the value of the property, not null
   */
  public MarginCalcMode getMode() {
    return mode;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valuation date of the calculation.
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
   * Gets the currency that the calculation was performed in, as an ISO 4217 three letter currency code.
   * @return the value of the property, not null
   */
  public String getCalculationCurrency() {
    return calculationCurrency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether to apply the client multiplier.
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
   * Gets the result of the margin calculation
   * @return the optional value of the property, not null
   */
  public Optional<MarginSummary> getMargin() {
    return Optional.ofNullable(margin);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the detailed breakdown of the margin calculation.
   * The structure varies by CCP.
   * @return the optional value of the property, not null
   */
  public Optional<MarginDetail> getMarginDetail() {
    return Optional.ofNullable(marginDetail);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valuation of the trades, including present value, delta and gamma results.
   * Sensitivity-based inputs are not valued.
   * @return the optional value of the property, not null
   */
  public Optional<TradeValuations> getTradeValuations() {
    return Optional.ofNullable(tradeValuations);
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
      MarginCalcResult other = (MarginCalcResult) obj;
      return JodaBeanUtils.equal(status, other.status) &&
          JodaBeanUtils.equal(calculationTypes, other.calculationTypes) &&
          JodaBeanUtils.equal(mode, other.mode) &&
          JodaBeanUtils.equal(valuationDate, other.valuationDate) &&
          JodaBeanUtils.equal(reportingCurrency, other.reportingCurrency) &&
          JodaBeanUtils.equal(calculationCurrency, other.calculationCurrency) &&
          (applyClientMultiplier == other.applyClientMultiplier) &&
          JodaBeanUtils.equal(portfolioItems, other.portfolioItems) &&
          JodaBeanUtils.equal(margin, other.margin) &&
          JodaBeanUtils.equal(marginDetail, other.marginDetail) &&
          JodaBeanUtils.equal(tradeValuations, other.tradeValuations) &&
          JodaBeanUtils.equal(failures, other.failures);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(status);
    hash = hash * 31 + JodaBeanUtils.hashCode(calculationTypes);
    hash = hash * 31 + JodaBeanUtils.hashCode(mode);
    hash = hash * 31 + JodaBeanUtils.hashCode(valuationDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(reportingCurrency);
    hash = hash * 31 + JodaBeanUtils.hashCode(calculationCurrency);
    hash = hash * 31 + JodaBeanUtils.hashCode(applyClientMultiplier);
    hash = hash * 31 + JodaBeanUtils.hashCode(portfolioItems);
    hash = hash * 31 + JodaBeanUtils.hashCode(margin);
    hash = hash * 31 + JodaBeanUtils.hashCode(marginDetail);
    hash = hash * 31 + JodaBeanUtils.hashCode(tradeValuations);
    hash = hash * 31 + JodaBeanUtils.hashCode(failures);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(416);
    buf.append("MarginCalcResult{");
    buf.append("status").append('=').append(JodaBeanUtils.toString(status)).append(',').append(' ');
    buf.append("calculationTypes").append('=').append(JodaBeanUtils.toString(calculationTypes)).append(',').append(' ');
    buf.append("mode").append('=').append(JodaBeanUtils.toString(mode)).append(',').append(' ');
    buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(valuationDate)).append(',').append(' ');
    buf.append("reportingCurrency").append('=').append(JodaBeanUtils.toString(reportingCurrency)).append(',').append(' ');
    buf.append("calculationCurrency").append('=').append(JodaBeanUtils.toString(calculationCurrency)).append(',').append(' ');
    buf.append("applyClientMultiplier").append('=').append(JodaBeanUtils.toString(applyClientMultiplier)).append(',').append(' ');
    buf.append("portfolioItems").append('=').append(JodaBeanUtils.toString(portfolioItems)).append(',').append(' ');
    buf.append("margin").append('=').append(JodaBeanUtils.toString(margin)).append(',').append(' ');
    buf.append("marginDetail").append('=').append(JodaBeanUtils.toString(marginDetail)).append(',').append(' ');
    buf.append("tradeValuations").append('=').append(JodaBeanUtils.toString(tradeValuations)).append(',').append(' ');
    buf.append("failures").append('=').append(JodaBeanUtils.toString(failures));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code MarginCalcResult}.
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
        this, "status", MarginCalcResult.class, MarginCalcResultStatus.class);
    /**
     * The meta-property for the {@code calculationTypes} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<MarginCalcType>> calculationTypes = DirectMetaProperty.ofImmutable(
        this, "calculationTypes", MarginCalcResult.class, (Class) Set.class);
    /**
     * The meta-property for the {@code mode} property.
     */
    private final MetaProperty<MarginCalcMode> mode = DirectMetaProperty.ofImmutable(
        this, "mode", MarginCalcResult.class, MarginCalcMode.class);
    /**
     * The meta-property for the {@code valuationDate} property.
     */
    private final MetaProperty<LocalDate> valuationDate = DirectMetaProperty.ofImmutable(
        this, "valuationDate", MarginCalcResult.class, LocalDate.class);
    /**
     * The meta-property for the {@code reportingCurrency} property.
     */
    private final MetaProperty<String> reportingCurrency = DirectMetaProperty.ofImmutable(
        this, "reportingCurrency", MarginCalcResult.class, String.class);
    /**
     * The meta-property for the {@code calculationCurrency} property.
     */
    private final MetaProperty<String> calculationCurrency = DirectMetaProperty.ofImmutable(
        this, "calculationCurrency", MarginCalcResult.class, String.class);
    /**
     * The meta-property for the {@code applyClientMultiplier} property.
     */
    private final MetaProperty<Boolean> applyClientMultiplier = DirectMetaProperty.ofImmutable(
        this, "applyClientMultiplier", MarginCalcResult.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code portfolioItems} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<PortfolioItemSummary>> portfolioItems = DirectMetaProperty.ofImmutable(
        this, "portfolioItems", MarginCalcResult.class, (Class) List.class);
    /**
     * The meta-property for the {@code margin} property.
     */
    private final MetaProperty<MarginSummary> margin = DirectMetaProperty.ofImmutable(
        this, "margin", MarginCalcResult.class, MarginSummary.class);
    /**
     * The meta-property for the {@code marginDetail} property.
     */
    private final MetaProperty<MarginDetail> marginDetail = DirectMetaProperty.ofImmutable(
        this, "marginDetail", MarginCalcResult.class, MarginDetail.class);
    /**
     * The meta-property for the {@code tradeValuations} property.
     */
    private final MetaProperty<TradeValuations> tradeValuations = DirectMetaProperty.ofImmutable(
        this, "tradeValuations", MarginCalcResult.class, TradeValuations.class);
    /**
     * The meta-property for the {@code failures} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<MarginError>> failures = DirectMetaProperty.ofImmutable(
        this, "failures", MarginCalcResult.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "status",
        "calculationTypes",
        "mode",
        "valuationDate",
        "reportingCurrency",
        "calculationCurrency",
        "applyClientMultiplier",
        "portfolioItems",
        "margin",
        "marginDetail",
        "tradeValuations",
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
        case 3357091:  // mode
          return mode;
        case 113107279:  // valuationDate
          return valuationDate;
        case -1287844769:  // reportingCurrency
          return reportingCurrency;
        case 1466784250:  // calculationCurrency
          return calculationCurrency;
        case 1555658618:  // applyClientMultiplier
          return applyClientMultiplier;
        case 110493528:  // portfolioItems
          return portfolioItems;
        case -1081309778:  // margin
          return margin;
        case -241168481:  // marginDetail
          return marginDetail;
        case 1449179926:  // tradeValuations
          return tradeValuations;
        case 675938345:  // failures
          return failures;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends MarginCalcResult> builder() {
      return new MarginCalcResult.Builder();
    }

    @Override
    public Class<? extends MarginCalcResult> beanType() {
      return MarginCalcResult.class;
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
          return ((MarginCalcResult) bean).getStatus();
        case 755457840:  // calculationTypes
          return ((MarginCalcResult) bean).getCalculationTypes();
        case 3357091:  // mode
          return ((MarginCalcResult) bean).getMode();
        case 113107279:  // valuationDate
          return ((MarginCalcResult) bean).getValuationDate();
        case -1287844769:  // reportingCurrency
          return ((MarginCalcResult) bean).getReportingCurrency();
        case 1466784250:  // calculationCurrency
          return ((MarginCalcResult) bean).getCalculationCurrency();
        case 1555658618:  // applyClientMultiplier
          return ((MarginCalcResult) bean).isApplyClientMultiplier();
        case 110493528:  // portfolioItems
          return ((MarginCalcResult) bean).getPortfolioItems();
        case -1081309778:  // margin
          return ((MarginCalcResult) bean).margin;
        case -241168481:  // marginDetail
          return ((MarginCalcResult) bean).marginDetail;
        case 1449179926:  // tradeValuations
          return ((MarginCalcResult) bean).tradeValuations;
        case 675938345:  // failures
          return ((MarginCalcResult) bean).getFailures();
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
   * The bean-builder for {@code MarginCalcResult}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<MarginCalcResult> {

    private MarginCalcResultStatus status;
    private Set<MarginCalcType> calculationTypes = Collections.emptySet();
    private MarginCalcMode mode;
    private LocalDate valuationDate;
    private String reportingCurrency;
    private String calculationCurrency;
    private boolean applyClientMultiplier;
    private List<PortfolioItemSummary> portfolioItems = Collections.emptyList();
    private MarginSummary margin;
    private MarginDetail marginDetail;
    private TradeValuations tradeValuations;
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
        case 3357091:  // mode
          return mode;
        case 113107279:  // valuationDate
          return valuationDate;
        case -1287844769:  // reportingCurrency
          return reportingCurrency;
        case 1466784250:  // calculationCurrency
          return calculationCurrency;
        case 1555658618:  // applyClientMultiplier
          return applyClientMultiplier;
        case 110493528:  // portfolioItems
          return portfolioItems;
        case -1081309778:  // margin
          return margin;
        case -241168481:  // marginDetail
          return marginDetail;
        case 1449179926:  // tradeValuations
          return tradeValuations;
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
        case 3357091:  // mode
          this.mode = (MarginCalcMode) newValue;
          break;
        case 113107279:  // valuationDate
          this.valuationDate = (LocalDate) newValue;
          break;
        case -1287844769:  // reportingCurrency
          this.reportingCurrency = (String) newValue;
          break;
        case 1466784250:  // calculationCurrency
          this.calculationCurrency = (String) newValue;
          break;
        case 1555658618:  // applyClientMultiplier
          this.applyClientMultiplier = (Boolean) newValue;
          break;
        case 110493528:  // portfolioItems
          this.portfolioItems = (List<PortfolioItemSummary>) newValue;
          break;
        case -1081309778:  // margin
          this.margin = (MarginSummary) newValue;
          break;
        case -241168481:  // marginDetail
          this.marginDetail = (MarginDetail) newValue;
          break;
        case 1449179926:  // tradeValuations
          this.tradeValuations = (TradeValuations) newValue;
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
    public MarginCalcResult build() {
      return new MarginCalcResult(
          status,
          calculationTypes,
          mode,
          valuationDate,
          reportingCurrency,
          calculationCurrency,
          applyClientMultiplier,
          portfolioItems,
          margin,
          marginDetail,
          tradeValuations,
          failures);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(416);
      buf.append("MarginCalcResult.Builder{");
      buf.append("status").append('=').append(JodaBeanUtils.toString(status)).append(',').append(' ');
      buf.append("calculationTypes").append('=').append(JodaBeanUtils.toString(calculationTypes)).append(',').append(' ');
      buf.append("mode").append('=').append(JodaBeanUtils.toString(mode)).append(',').append(' ');
      buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(valuationDate)).append(',').append(' ');
      buf.append("reportingCurrency").append('=').append(JodaBeanUtils.toString(reportingCurrency)).append(',').append(' ');
      buf.append("calculationCurrency").append('=').append(JodaBeanUtils.toString(calculationCurrency)).append(',').append(' ');
      buf.append("applyClientMultiplier").append('=').append(JodaBeanUtils.toString(applyClientMultiplier)).append(',').append(' ');
      buf.append("portfolioItems").append('=').append(JodaBeanUtils.toString(portfolioItems)).append(',').append(' ');
      buf.append("margin").append('=').append(JodaBeanUtils.toString(margin)).append(',').append(' ');
      buf.append("marginDetail").append('=').append(JodaBeanUtils.toString(marginDetail)).append(',').append(' ');
      buf.append("tradeValuations").append('=').append(JodaBeanUtils.toString(tradeValuations)).append(',').append(' ');
      buf.append("failures").append('=').append(JodaBeanUtils.toString(failures));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
