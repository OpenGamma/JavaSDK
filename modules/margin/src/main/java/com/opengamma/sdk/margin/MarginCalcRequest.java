/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutableConstructor;
import org.joda.beans.gen.ImmutableDefaults;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.ser.JodaBeanSer;

/**
 * Request to pass to the service.
 */
@BeanDefinition(metaScope = "private")
@SuppressWarnings("deprecation")
public final class MarginCalcRequest implements ImmutableBean {

  /**
   * The type of calculation to perform.
   * <p>
   * This has been replaced by {@link #getCalculationTypes()}, which provides
   * more control and more features.
   * If {@code PARSE_INPUTS} or {@code FULL} is set, the calculation types will include {@code PORTFOLIO_SUMMARY}.
   * If {@code STANDARD} or {@code FULL} is set, the calculation types will include {@code MARGIN}.
   * 
   * @deprecated Use {@link #getCalculationTypes()} instead
   */
  @Deprecated
  @PropertyDefinition(get = "field", toStringStyle = "omit")
  private final MarginCalcRequestType type;
  /**
   * The types of information to include in the output.
   * <p>
   * If an empty set is passed in, the output type {@link MarginCalcType#MARGIN MARGIN} will be used.
   */
  @PropertyDefinition(validate = "notNull")
  private final Set<MarginCalcType> calculationTypes;
  /**
   * The mode of the calculation, defaulted to {@code SPOT}.
   */
  @PropertyDefinition(validate = "notNull")
  private final MarginCalcMode mode;
  /**
   * The valuation date for which the portfolio will be processed.
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate valuationDate;
  /**
   * Whether to apply the client multiplier, default false.
   */
  @PropertyDefinition
  private final boolean applyClientMultiplier;
  /**
   * The currency that the result is reported in, as an ISO 4217 three letter currency code.
   */
  @PropertyDefinition(validate = "notNull")
  private final String reportingCurrency;
  /**
   * The optional currency that the calculation is performed in, as an ISO 4217 three letter currency code.
   * If omitted, the calculation currency will be inferred from the reportingCurrency.
   */
  @PropertyDefinition(get = "optional")
  private final String calculationCurrency;
  /**
   * The portfolio data, where each entry typically represents a CSV, TSV or XML file.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<PortfolioDataFile> portfolioData;
  /**
   * The regular expression used to select the party in any FpML input.
   * The regular expression is matched against the content of the {@code partyId} elements.
   * If this is not specified, FpML cannot be parsed.
   */
  @PropertyDefinition(get = "optional")
  private final String fpmlPartySelectionRegex;
  /**
   * To have self-referencing margin calculated correctly, we need to pass in (if specified) the clearing member code.
   */
  @PropertyDefinition(get = "optional")
  private final String portfolioOwnerCode;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance that requests a standard margin calculation.
   * <p>
   * The list of portfolio data should be either {@link PortfolioDataFile} instances
   * or Strata {@code CalculationTarget} instances, such as trades or positions.
   * Defaults the {@link MarginCalcMode} to MarginCalcMode.SPOT.
   *
   * @param valuationDate  the valuation date
   * @param reportingCurrency  the reporting currency
   * @param portfolioData  the portfolio data, which can be {@link PortfolioDataFile} or Strata trades
   * @param fpmlPartySelectionRegex  the regular expression used to select the party in any FpML input
   * @return the request
   */
  public static MarginCalcRequest of(
      LocalDate valuationDate,
      String reportingCurrency,
      List<? extends Bean> portfolioData,
      String fpmlPartySelectionRegex) {

    List<PortfolioDataFile> files = convertPortfolioData(portfolioData);
    return new MarginCalcRequest(
        MarginCalcRequestType.STANDARD,
        new HashSet<>(),
        MarginCalcMode.SPOT,
        valuationDate,
        false,
        reportingCurrency,
        null,
        files,
        fpmlPartySelectionRegex,
        null);
  }

  /**
   * Obtains an instance that requests a standard margin calculation.
   * <p>
   * The list of portfolio data should be either {@link PortfolioDataFile} instances
   * or Strata {@code CalculationTarget} instances, such as trades or positions.
   *
   * @param valuationDate  the valuation date
   * @param reportingCurrency  the reporting currency
   * @param mode  the mode of the calculation
   * @param portfolioData  the portfolio data, which can be {@link PortfolioDataFile} or Strata trades
   * @param fpmlPartySelectionRegex  the regular expression used to select the party in any FpML input
   * @return the request
   */
  public static MarginCalcRequest of(
      LocalDate valuationDate,
      String reportingCurrency,
      MarginCalcMode mode,
      List<? extends Bean> portfolioData,
      String fpmlPartySelectionRegex) {

    List<PortfolioDataFile> files = convertPortfolioData(portfolioData);
    return new MarginCalcRequest(
        MarginCalcRequestType.STANDARD,
        new HashSet<>(),
        mode,
        valuationDate,
        false,
        reportingCurrency,
        null,
        files,
        fpmlPartySelectionRegex,
        null);
  }

  /**
   * Obtains an instance that requests a standard margin calculation.
   * <p>
   * The list of portfolio data should be either {@link PortfolioDataFile} instances
   * or Strata {@code CalculationTarget} instances, such as trades or positions.
   * Defaults the {@link MarginCalcMode} to MarginCalcMode.SPOT.
   *
   * @param valuationDate  the valuation date
   * @param reportingCurrency  the reporting currency
   * @param portfolioData  the portfolio data, which can be {@link PortfolioDataFile} or Strata trades
   * @return the request
   */
  public static MarginCalcRequest of(
      LocalDate valuationDate,
      String reportingCurrency,
      List<? extends Bean> portfolioData) {

    List<PortfolioDataFile> files = convertPortfolioData(portfolioData);
    return new MarginCalcRequest(
        MarginCalcRequestType.STANDARD,
        new HashSet<>(),
        MarginCalcMode.SPOT,
        valuationDate,
        false,
        reportingCurrency,
        null,
        files,
        null,
        null);
  }

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance that requests a margin calculation.
   * <p>
   * The list of portfolio data should be either {@link PortfolioDataFile} instances
   * or Strata {@code CalculationTarget} instances, such as trades or positions.
   *
   * @param valuationDate  the valuation date
   * @param reportingCurrency  the reporting currency
   * @param mode  the mode of the calculation
   * @param portfolioData  the portfolio data, which can be {@link PortfolioDataFile} or Strata trades
   * @param type  the type of request to perform
   * @param applyClientModifier  whether to apply the client modifier
   * @return the request
   * @deprecated Use the builder
   */
  @Deprecated
  public static MarginCalcRequest of(
      LocalDate valuationDate,
      String reportingCurrency,
      MarginCalcMode mode,
      List<? extends Bean> portfolioData,
      MarginCalcRequestType type,
      boolean applyClientModifier) {

    List<PortfolioDataFile> files = convertPortfolioData(portfolioData);
    return new MarginCalcRequest(
        type,
        new HashSet<>(),
        mode,
        valuationDate,
        applyClientModifier,
        reportingCurrency,
        null,
        files,
        null,
        null);
  }

  /**
   * Obtains an instance that requests a margin calculation.
   * <p>
   * The list of portfolio data should be either {@link PortfolioDataFile} instances
   * or Strata {@code CalculationTarget} instances, such as trades or positions.
   * Defaults the {@link MarginCalcMode} to MarginCalcMode.SPOT.
   *
   * @param valuationDate  the valuation date
   * @param reportingCurrency  the reporting currency
   * @param portfolioData  the portfolio data, which can be {@link PortfolioDataFile} or Strata trades
   * @param type  the type of request to perform
   * @param applyClientModifier  whether to apply the client modifier
   * @return the request
   * @deprecated Use the builder
   */
  @Deprecated
  public static MarginCalcRequest of(
      LocalDate valuationDate,
      String reportingCurrency,
      List<? extends Bean> portfolioData,
      MarginCalcRequestType type,
      boolean applyClientModifier) {

    List<PortfolioDataFile> files = convertPortfolioData(portfolioData);
    return new MarginCalcRequest(
        type,
        new HashSet<>(),
        MarginCalcMode.SPOT,
        valuationDate,
        applyClientModifier,
        reportingCurrency,
        null,
        files,
        null,
        null);
  }

  private static List<PortfolioDataFile> convertPortfolioData(List<? extends Bean> portfolioData) {
    List<PortfolioDataFile> files = new ArrayList<>();
    for (Bean bean : portfolioData) {
      if (bean instanceof PortfolioDataFile) {
        files.add((PortfolioDataFile) bean);
      } else {
        String xml = JodaBeanSer.COMPACT.xmlWriter().write(bean);
        files.add(PortfolioDataFile.of(bean.getClass().getSimpleName() + ".xml", xml));
      }
    }
    return files;
  }

  @ImmutableDefaults
  private static void applyDefaults(Builder builder) {
    builder.mode = MarginCalcMode.SPOT;
  }

  @ImmutableConstructor
  private MarginCalcRequest(
      MarginCalcRequestType type,
      Set<MarginCalcType> calculationTypes,
      MarginCalcMode mode,
      LocalDate valuationDate,
      boolean applyClientMultiplier,
      String reportingCurrency,
      String calculationCurrency,
      List<PortfolioDataFile> portfolioData,
      String fpmlPartySelectionRegex,
      String portfolioOwnerCode) {
    JodaBeanUtils.notNull(calculationTypes, "calculationTypes");
    JodaBeanUtils.notNull(mode, "mode");
    JodaBeanUtils.notNull(valuationDate, "valuationDate");
    JodaBeanUtils.notNull(reportingCurrency, "reportingCurrency");
    JodaBeanUtils.notNull(portfolioData, "portfolioData");

    // ensure that the type field is always null, this approach means that it will still have
    // a builder method, but the value of the field will not be sent to the server
    this.type = null;
    // build set of calculation types, allowing that old type might be null
    HashSet<MarginCalcType> types = new HashSet<>(calculationTypes);
    if (type != null) {
      types.addAll(type.toCalculationTypes());
    }
    this.calculationTypes = Collections.unmodifiableSet(types);
    this.mode = mode;
    this.valuationDate = valuationDate;
    this.applyClientMultiplier = applyClientMultiplier;
    this.reportingCurrency = reportingCurrency;
    this.calculationCurrency = calculationCurrency;
    this.portfolioData = Collections.unmodifiableList(new ArrayList<>(portfolioData));
    this.fpmlPartySelectionRegex = fpmlPartySelectionRegex;
    this.portfolioOwnerCode = portfolioOwnerCode;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the type of calculation to perform.
   * <p>
   * This has been replaced by {@link #getCalculationTypes()}, which provides
   * more control and more features.
   * If {@code PARSE_INPUTS} or {@code FULL} is set, the calculation types will include {@code PORTFOLIO_SUMMARY}.
   * If {@code STANDARD} or {@code FULL} is set, the calculation types will include {@code MARGIN}.
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
   * The meta-bean for {@code MarginCalcRequest}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return MarginCalcRequest.Meta.INSTANCE;
  }

  static {
    MetaBean.register(MarginCalcRequest.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static MarginCalcRequest.Builder builder() {
    return new MarginCalcRequest.Builder();
  }

  @Override
  public MetaBean metaBean() {
    return MarginCalcRequest.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the types of information to include in the output.
   * <p>
   * If an empty set is passed in, the output type {@link MarginCalcType#MARGIN MARGIN} will be used.
   * @return the value of the property, not null
   */
  public Set<MarginCalcType> getCalculationTypes() {
    return calculationTypes;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the mode of the calculation, defaulted to {@code SPOT}.
   * @return the value of the property, not null
   */
  public MarginCalcMode getMode() {
    return mode;
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
   * Gets whether to apply the client multiplier, default false.
   * @return the value of the property
   */
  public boolean isApplyClientMultiplier() {
    return applyClientMultiplier;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency that the result is reported in, as an ISO 4217 three letter currency code.
   * @return the value of the property, not null
   */
  public String getReportingCurrency() {
    return reportingCurrency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the optional currency that the calculation is performed in, as an ISO 4217 three letter currency code.
   * If omitted, the calculation currency will be inferred from the reportingCurrency.
   * @return the optional value of the property, not null
   */
  public Optional<String> getCalculationCurrency() {
    return Optional.ofNullable(calculationCurrency);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio data, where each entry typically represents a CSV, TSV or XML file.
   * @return the value of the property, not null
   */
  public List<PortfolioDataFile> getPortfolioData() {
    return portfolioData;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the regular expression used to select the party in any FpML input.
   * The regular expression is matched against the content of the {@code partyId} elements.
   * If this is not specified, FpML cannot be parsed.
   * @return the optional value of the property, not null
   */
  public Optional<String> getFpmlPartySelectionRegex() {
    return Optional.ofNullable(fpmlPartySelectionRegex);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets to have self-referencing margin calculated correctly, we need to pass in (if specified) the clearing member code.
   * @return the optional value of the property, not null
   */
  public Optional<String> getPortfolioOwnerCode() {
    return Optional.ofNullable(portfolioOwnerCode);
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
      MarginCalcRequest other = (MarginCalcRequest) obj;
      return JodaBeanUtils.equal(type, other.type) &&
          JodaBeanUtils.equal(calculationTypes, other.calculationTypes) &&
          JodaBeanUtils.equal(mode, other.mode) &&
          JodaBeanUtils.equal(valuationDate, other.valuationDate) &&
          (applyClientMultiplier == other.applyClientMultiplier) &&
          JodaBeanUtils.equal(reportingCurrency, other.reportingCurrency) &&
          JodaBeanUtils.equal(calculationCurrency, other.calculationCurrency) &&
          JodaBeanUtils.equal(portfolioData, other.portfolioData) &&
          JodaBeanUtils.equal(fpmlPartySelectionRegex, other.fpmlPartySelectionRegex) &&
          JodaBeanUtils.equal(portfolioOwnerCode, other.portfolioOwnerCode);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(type);
    hash = hash * 31 + JodaBeanUtils.hashCode(calculationTypes);
    hash = hash * 31 + JodaBeanUtils.hashCode(mode);
    hash = hash * 31 + JodaBeanUtils.hashCode(valuationDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(applyClientMultiplier);
    hash = hash * 31 + JodaBeanUtils.hashCode(reportingCurrency);
    hash = hash * 31 + JodaBeanUtils.hashCode(calculationCurrency);
    hash = hash * 31 + JodaBeanUtils.hashCode(portfolioData);
    hash = hash * 31 + JodaBeanUtils.hashCode(fpmlPartySelectionRegex);
    hash = hash * 31 + JodaBeanUtils.hashCode(portfolioOwnerCode);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(320);
    buf.append("MarginCalcRequest{");
    buf.append("calculationTypes").append('=').append(calculationTypes).append(',').append(' ');
    buf.append("mode").append('=').append(mode).append(',').append(' ');
    buf.append("valuationDate").append('=').append(valuationDate).append(',').append(' ');
    buf.append("applyClientMultiplier").append('=').append(applyClientMultiplier).append(',').append(' ');
    buf.append("reportingCurrency").append('=').append(reportingCurrency).append(',').append(' ');
    buf.append("calculationCurrency").append('=').append(calculationCurrency).append(',').append(' ');
    buf.append("portfolioData").append('=').append(portfolioData).append(',').append(' ');
    buf.append("fpmlPartySelectionRegex").append('=').append(fpmlPartySelectionRegex).append(',').append(' ');
    buf.append("portfolioOwnerCode").append('=').append(JodaBeanUtils.toString(portfolioOwnerCode));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code MarginCalcRequest}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code type} property.
     */
    private final MetaProperty<MarginCalcRequestType> type = DirectMetaProperty.ofImmutable(
        this, "type", MarginCalcRequest.class, MarginCalcRequestType.class);
    /**
     * The meta-property for the {@code calculationTypes} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<MarginCalcType>> calculationTypes = DirectMetaProperty.ofImmutable(
        this, "calculationTypes", MarginCalcRequest.class, (Class) Set.class);
    /**
     * The meta-property for the {@code mode} property.
     */
    private final MetaProperty<MarginCalcMode> mode = DirectMetaProperty.ofImmutable(
        this, "mode", MarginCalcRequest.class, MarginCalcMode.class);
    /**
     * The meta-property for the {@code valuationDate} property.
     */
    private final MetaProperty<LocalDate> valuationDate = DirectMetaProperty.ofImmutable(
        this, "valuationDate", MarginCalcRequest.class, LocalDate.class);
    /**
     * The meta-property for the {@code applyClientMultiplier} property.
     */
    private final MetaProperty<Boolean> applyClientMultiplier = DirectMetaProperty.ofImmutable(
        this, "applyClientMultiplier", MarginCalcRequest.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code reportingCurrency} property.
     */
    private final MetaProperty<String> reportingCurrency = DirectMetaProperty.ofImmutable(
        this, "reportingCurrency", MarginCalcRequest.class, String.class);
    /**
     * The meta-property for the {@code calculationCurrency} property.
     */
    private final MetaProperty<String> calculationCurrency = DirectMetaProperty.ofImmutable(
        this, "calculationCurrency", MarginCalcRequest.class, String.class);
    /**
     * The meta-property for the {@code portfolioData} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<PortfolioDataFile>> portfolioData = DirectMetaProperty.ofImmutable(
        this, "portfolioData", MarginCalcRequest.class, (Class) List.class);
    /**
     * The meta-property for the {@code fpmlPartySelectionRegex} property.
     */
    private final MetaProperty<String> fpmlPartySelectionRegex = DirectMetaProperty.ofImmutable(
        this, "fpmlPartySelectionRegex", MarginCalcRequest.class, String.class);
    /**
     * The meta-property for the {@code portfolioOwnerCode} property.
     */
    private final MetaProperty<String> portfolioOwnerCode = DirectMetaProperty.ofImmutable(
        this, "portfolioOwnerCode", MarginCalcRequest.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "type",
        "calculationTypes",
        "mode",
        "valuationDate",
        "applyClientMultiplier",
        "reportingCurrency",
        "calculationCurrency",
        "portfolioData",
        "fpmlPartySelectionRegex",
        "portfolioOwnerCode");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          return type;
        case 755457840:  // calculationTypes
          return calculationTypes;
        case 3357091:  // mode
          return mode;
        case 113107279:  // valuationDate
          return valuationDate;
        case 1555658618:  // applyClientMultiplier
          return applyClientMultiplier;
        case -1287844769:  // reportingCurrency
          return reportingCurrency;
        case 1466784250:  // calculationCurrency
          return calculationCurrency;
        case -689339118:  // portfolioData
          return portfolioData;
        case 527038456:  // fpmlPartySelectionRegex
          return fpmlPartySelectionRegex;
        case 1108872920:  // portfolioOwnerCode
          return portfolioOwnerCode;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public MarginCalcRequest.Builder builder() {
      return new MarginCalcRequest.Builder();
    }

    @Override
    public Class<? extends MarginCalcRequest> beanType() {
      return MarginCalcRequest.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          return ((MarginCalcRequest) bean).type;
        case 755457840:  // calculationTypes
          return ((MarginCalcRequest) bean).getCalculationTypes();
        case 3357091:  // mode
          return ((MarginCalcRequest) bean).getMode();
        case 113107279:  // valuationDate
          return ((MarginCalcRequest) bean).getValuationDate();
        case 1555658618:  // applyClientMultiplier
          return ((MarginCalcRequest) bean).isApplyClientMultiplier();
        case -1287844769:  // reportingCurrency
          return ((MarginCalcRequest) bean).getReportingCurrency();
        case 1466784250:  // calculationCurrency
          return ((MarginCalcRequest) bean).calculationCurrency;
        case -689339118:  // portfolioData
          return ((MarginCalcRequest) bean).getPortfolioData();
        case 527038456:  // fpmlPartySelectionRegex
          return ((MarginCalcRequest) bean).fpmlPartySelectionRegex;
        case 1108872920:  // portfolioOwnerCode
          return ((MarginCalcRequest) bean).portfolioOwnerCode;
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
   * The bean-builder for {@code MarginCalcRequest}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<MarginCalcRequest> {

    private MarginCalcRequestType type;
    private Set<MarginCalcType> calculationTypes = Collections.emptySet();
    private MarginCalcMode mode;
    private LocalDate valuationDate;
    private boolean applyClientMultiplier;
    private String reportingCurrency;
    private String calculationCurrency;
    private List<PortfolioDataFile> portfolioData = Collections.emptyList();
    private String fpmlPartySelectionRegex;
    private String portfolioOwnerCode;

    /**
     * Restricted constructor.
     */
    private Builder() {
      applyDefaults(this);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(MarginCalcRequest beanToCopy) {
      this.type = beanToCopy.type;
      this.calculationTypes = new HashSet<>(beanToCopy.getCalculationTypes());
      this.mode = beanToCopy.getMode();
      this.valuationDate = beanToCopy.getValuationDate();
      this.applyClientMultiplier = beanToCopy.isApplyClientMultiplier();
      this.reportingCurrency = beanToCopy.getReportingCurrency();
      this.calculationCurrency = beanToCopy.calculationCurrency;
      this.portfolioData = new ArrayList<>(beanToCopy.getPortfolioData());
      this.fpmlPartySelectionRegex = beanToCopy.fpmlPartySelectionRegex;
      this.portfolioOwnerCode = beanToCopy.portfolioOwnerCode;
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          return type;
        case 755457840:  // calculationTypes
          return calculationTypes;
        case 3357091:  // mode
          return mode;
        case 113107279:  // valuationDate
          return valuationDate;
        case 1555658618:  // applyClientMultiplier
          return applyClientMultiplier;
        case -1287844769:  // reportingCurrency
          return reportingCurrency;
        case 1466784250:  // calculationCurrency
          return calculationCurrency;
        case -689339118:  // portfolioData
          return portfolioData;
        case 527038456:  // fpmlPartySelectionRegex
          return fpmlPartySelectionRegex;
        case 1108872920:  // portfolioOwnerCode
          return portfolioOwnerCode;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3575610:  // type
          this.type = (MarginCalcRequestType) newValue;
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
        case 1555658618:  // applyClientMultiplier
          this.applyClientMultiplier = (Boolean) newValue;
          break;
        case -1287844769:  // reportingCurrency
          this.reportingCurrency = (String) newValue;
          break;
        case 1466784250:  // calculationCurrency
          this.calculationCurrency = (String) newValue;
          break;
        case -689339118:  // portfolioData
          this.portfolioData = (List<PortfolioDataFile>) newValue;
          break;
        case 527038456:  // fpmlPartySelectionRegex
          this.fpmlPartySelectionRegex = (String) newValue;
          break;
        case 1108872920:  // portfolioOwnerCode
          this.portfolioOwnerCode = (String) newValue;
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
    public MarginCalcRequest build() {
      return new MarginCalcRequest(
          type,
          calculationTypes,
          mode,
          valuationDate,
          applyClientMultiplier,
          reportingCurrency,
          calculationCurrency,
          portfolioData,
          fpmlPartySelectionRegex,
          portfolioOwnerCode);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the type of calculation to perform.
     * <p>
     * This has been replaced by {@link #getCalculationTypes()}, which provides
     * more control and more features.
     * If {@code PARSE_INPUTS} or {@code FULL} is set, the calculation types will include {@code PORTFOLIO_SUMMARY}.
     * If {@code STANDARD} or {@code FULL} is set, the calculation types will include {@code MARGIN}.
     * 
     * @param type  the new value
     * @return this, for chaining, not null
     * @deprecated Use {@link #getCalculationTypes()} instead
     */
    @Deprecated
    public Builder type(MarginCalcRequestType type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the types of information to include in the output.
     * <p>
     * If an empty set is passed in, the output type {@link MarginCalcType#MARGIN MARGIN} will be used.
     * @param calculationTypes  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder calculationTypes(Set<MarginCalcType> calculationTypes) {
      JodaBeanUtils.notNull(calculationTypes, "calculationTypes");
      this.calculationTypes = calculationTypes;
      return this;
    }

    /**
     * Sets the {@code calculationTypes} property in the builder
     * from an array of objects.
     * @param calculationTypes  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder calculationTypes(MarginCalcType... calculationTypes) {
      return calculationTypes(new LinkedHashSet<MarginCalcType>(Arrays.asList(calculationTypes)));
    }

    /**
     * Sets the mode of the calculation, defaulted to {@code SPOT}.
     * @param mode  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder mode(MarginCalcMode mode) {
      JodaBeanUtils.notNull(mode, "mode");
      this.mode = mode;
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
     * Sets whether to apply the client multiplier, default false.
     * @param applyClientMultiplier  the new value
     * @return this, for chaining, not null
     */
    public Builder applyClientMultiplier(boolean applyClientMultiplier) {
      this.applyClientMultiplier = applyClientMultiplier;
      return this;
    }

    /**
     * Sets the currency that the result is reported in, as an ISO 4217 three letter currency code.
     * @param reportingCurrency  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder reportingCurrency(String reportingCurrency) {
      JodaBeanUtils.notNull(reportingCurrency, "reportingCurrency");
      this.reportingCurrency = reportingCurrency;
      return this;
    }

    /**
     * Sets the optional currency that the calculation is performed in, as an ISO 4217 three letter currency code.
     * If omitted, the calculation currency will be inferred from the reportingCurrency.
     * @param calculationCurrency  the new value
     * @return this, for chaining, not null
     */
    public Builder calculationCurrency(String calculationCurrency) {
      this.calculationCurrency = calculationCurrency;
      return this;
    }

    /**
     * Sets the portfolio data, where each entry typically represents a CSV, TSV or XML file.
     * @param portfolioData  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder portfolioData(List<PortfolioDataFile> portfolioData) {
      JodaBeanUtils.notNull(portfolioData, "portfolioData");
      this.portfolioData = portfolioData;
      return this;
    }

    /**
     * Sets the {@code portfolioData} property in the builder
     * from an array of objects.
     * @param portfolioData  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder portfolioData(PortfolioDataFile... portfolioData) {
      return portfolioData(Arrays.asList(portfolioData));
    }

    /**
     * Sets the regular expression used to select the party in any FpML input.
     * The regular expression is matched against the content of the {@code partyId} elements.
     * If this is not specified, FpML cannot be parsed.
     * @param fpmlPartySelectionRegex  the new value
     * @return this, for chaining, not null
     */
    public Builder fpmlPartySelectionRegex(String fpmlPartySelectionRegex) {
      this.fpmlPartySelectionRegex = fpmlPartySelectionRegex;
      return this;
    }

    /**
     * Sets to have self-referencing margin calculated correctly, we need to pass in (if specified) the clearing member code.
     * @param portfolioOwnerCode  the new value
     * @return this, for chaining, not null
     */
    public Builder portfolioOwnerCode(String portfolioOwnerCode) {
      this.portfolioOwnerCode = portfolioOwnerCode;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(320);
      buf.append("MarginCalcRequest.Builder{");
      buf.append("calculationTypes").append('=').append(JodaBeanUtils.toString(calculationTypes)).append(',').append(' ');
      buf.append("mode").append('=').append(JodaBeanUtils.toString(mode)).append(',').append(' ');
      buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(valuationDate)).append(',').append(' ');
      buf.append("applyClientMultiplier").append('=').append(JodaBeanUtils.toString(applyClientMultiplier)).append(',').append(' ');
      buf.append("reportingCurrency").append('=').append(JodaBeanUtils.toString(reportingCurrency)).append(',').append(' ');
      buf.append("calculationCurrency").append('=').append(JodaBeanUtils.toString(calculationCurrency)).append(',').append(' ');
      buf.append("portfolioData").append('=').append(JodaBeanUtils.toString(portfolioData)).append(',').append(' ');
      buf.append("fpmlPartySelectionRegex").append('=').append(JodaBeanUtils.toString(fpmlPartySelectionRegex)).append(',').append(' ');
      buf.append("portfolioOwnerCode").append('=').append(JodaBeanUtils.toString(portfolioOwnerCode));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
