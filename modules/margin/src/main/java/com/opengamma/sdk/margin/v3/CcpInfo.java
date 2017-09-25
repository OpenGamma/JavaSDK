/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.v3;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * CCP information from the service.
 */
@BeanDefinition(builderScope = "private", metaScope = "private", factoryName = "of")
public final class CcpInfo implements ImmutableBean {
  /**
   * The list of valuation dates available for the CCP, may be empty.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<LocalDate> valuationDates;
  /**
   * The default currency of the CCP.
   */
  @PropertyDefinition(validate = "notBlank")
  private final String defaultCurrency;
  /**
   * The list of currencies that the report may be reported in, not empty.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<String> reportingCurrencies;
  /**
   * The list of currencies that the calculation can be performed in,
   * empty if it is intended to be inferred from the reporting currency.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<String> calculationCurrencies;

  //-------------------------------------------------------------------------
  /**
   * Gets the latest available valuation date.
   * 
   * @return the latest available valuation date
   * @throws IllegalArgumentException if there are no valid valuation dates
   */
  public LocalDate getLatestValuationDate() {
    return valuationDates.stream()
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No valuation dates are available for current CCP"));
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code CcpInfo}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return CcpInfo.Meta.INSTANCE;
  }

  static {
    MetaBean.register(CcpInfo.Meta.INSTANCE);
  }

  /**
   * Obtains an instance.
   * @param valuationDates  the value of the property, not null
   * @param defaultCurrency  the value of the property, not blank
   * @param reportingCurrencies  the value of the property, not null
   * @param calculationCurrencies  the value of the property, not null
   * @return the instance
   */
  public static CcpInfo of(
      List<LocalDate> valuationDates,
      String defaultCurrency,
      List<String> reportingCurrencies,
      List<String> calculationCurrencies) {
    return new CcpInfo(
      valuationDates,
      defaultCurrency,
      reportingCurrencies,
      calculationCurrencies);
  }

  private CcpInfo(
      List<LocalDate> valuationDates,
      String defaultCurrency,
      List<String> reportingCurrencies,
      List<String> calculationCurrencies) {
    JodaBeanUtils.notNull(valuationDates, "valuationDates");
    JodaBeanUtils.notBlank(defaultCurrency, "defaultCurrency");
    JodaBeanUtils.notNull(reportingCurrencies, "reportingCurrencies");
    JodaBeanUtils.notNull(calculationCurrencies, "calculationCurrencies");
    this.valuationDates = Collections.unmodifiableList(new ArrayList<>(valuationDates));
    this.defaultCurrency = defaultCurrency;
    this.reportingCurrencies = Collections.unmodifiableList(new ArrayList<>(reportingCurrencies));
    this.calculationCurrencies = Collections.unmodifiableList(new ArrayList<>(calculationCurrencies));
  }

  @Override
  public MetaBean metaBean() {
    return CcpInfo.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the list of valuation dates available for the CCP, may be empty.
   * @return the value of the property, not null
   */
  public List<LocalDate> getValuationDates() {
    return valuationDates;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the default currency of the CCP.
   * @return the value of the property, not blank
   */
  public String getDefaultCurrency() {
    return defaultCurrency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the list of currencies that the report may be reported in, not empty.
   * @return the value of the property, not null
   */
  public List<String> getReportingCurrencies() {
    return reportingCurrencies;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the list of currencies that the calculation can be performed in,
   * empty if it is intended to be inferred from the reporting currency.
   * @return the value of the property, not null
   */
  public List<String> getCalculationCurrencies() {
    return calculationCurrencies;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CcpInfo other = (CcpInfo) obj;
      return JodaBeanUtils.equal(valuationDates, other.valuationDates) &&
          JodaBeanUtils.equal(defaultCurrency, other.defaultCurrency) &&
          JodaBeanUtils.equal(reportingCurrencies, other.reportingCurrencies) &&
          JodaBeanUtils.equal(calculationCurrencies, other.calculationCurrencies);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(valuationDates);
    hash = hash * 31 + JodaBeanUtils.hashCode(defaultCurrency);
    hash = hash * 31 + JodaBeanUtils.hashCode(reportingCurrencies);
    hash = hash * 31 + JodaBeanUtils.hashCode(calculationCurrencies);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("CcpInfo{");
    buf.append("valuationDates").append('=').append(valuationDates).append(',').append(' ');
    buf.append("defaultCurrency").append('=').append(defaultCurrency).append(',').append(' ');
    buf.append("reportingCurrencies").append('=').append(reportingCurrencies).append(',').append(' ');
    buf.append("calculationCurrencies").append('=').append(JodaBeanUtils.toString(calculationCurrencies));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CcpInfo}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code valuationDates} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<LocalDate>> valuationDates = DirectMetaProperty.ofImmutable(
        this, "valuationDates", CcpInfo.class, (Class) List.class);
    /**
     * The meta-property for the {@code defaultCurrency} property.
     */
    private final MetaProperty<String> defaultCurrency = DirectMetaProperty.ofImmutable(
        this, "defaultCurrency", CcpInfo.class, String.class);
    /**
     * The meta-property for the {@code reportingCurrencies} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<String>> reportingCurrencies = DirectMetaProperty.ofImmutable(
        this, "reportingCurrencies", CcpInfo.class, (Class) List.class);
    /**
     * The meta-property for the {@code calculationCurrencies} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<String>> calculationCurrencies = DirectMetaProperty.ofImmutable(
        this, "calculationCurrencies", CcpInfo.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "valuationDates",
        "defaultCurrency",
        "reportingCurrencies",
        "calculationCurrencies");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -788641532:  // valuationDates
          return valuationDates;
        case -1721476750:  // defaultCurrency
          return defaultCurrency;
        case -668253891:  // reportingCurrencies
          return reportingCurrencies;
        case 830379032:  // calculationCurrencies
          return calculationCurrencies;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CcpInfo> builder() {
      return new CcpInfo.Builder();
    }

    @Override
    public Class<? extends CcpInfo> beanType() {
      return CcpInfo.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -788641532:  // valuationDates
          return ((CcpInfo) bean).getValuationDates();
        case -1721476750:  // defaultCurrency
          return ((CcpInfo) bean).getDefaultCurrency();
        case -668253891:  // reportingCurrencies
          return ((CcpInfo) bean).getReportingCurrencies();
        case 830379032:  // calculationCurrencies
          return ((CcpInfo) bean).getCalculationCurrencies();
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
   * The bean-builder for {@code CcpInfo}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<CcpInfo> {

    private List<LocalDate> valuationDates = Collections.emptyList();
    private String defaultCurrency;
    private List<String> reportingCurrencies = Collections.emptyList();
    private List<String> calculationCurrencies = Collections.emptyList();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -788641532:  // valuationDates
          return valuationDates;
        case -1721476750:  // defaultCurrency
          return defaultCurrency;
        case -668253891:  // reportingCurrencies
          return reportingCurrencies;
        case 830379032:  // calculationCurrencies
          return calculationCurrencies;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -788641532:  // valuationDates
          this.valuationDates = (List<LocalDate>) newValue;
          break;
        case -1721476750:  // defaultCurrency
          this.defaultCurrency = (String) newValue;
          break;
        case -668253891:  // reportingCurrencies
          this.reportingCurrencies = (List<String>) newValue;
          break;
        case 830379032:  // calculationCurrencies
          this.calculationCurrencies = (List<String>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public CcpInfo build() {
      return new CcpInfo(
          valuationDates,
          defaultCurrency,
          reportingCurrencies,
          calculationCurrencies);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(160);
      buf.append("CcpInfo.Builder{");
      buf.append("valuationDates").append('=').append(JodaBeanUtils.toString(valuationDates)).append(',').append(' ');
      buf.append("defaultCurrency").append('=').append(JodaBeanUtils.toString(defaultCurrency)).append(',').append(' ');
      buf.append("reportingCurrencies").append('=').append(JodaBeanUtils.toString(reportingCurrencies)).append(',').append(' ');
      buf.append("calculationCurrencies").append('=').append(JodaBeanUtils.toString(calculationCurrencies));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
