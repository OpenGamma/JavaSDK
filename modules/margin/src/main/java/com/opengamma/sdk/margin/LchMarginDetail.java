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

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.DerivedProperty;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

/**
 * A detailed breakdown of the LCH margin calculation.
 */
@BeanDefinition(builderScope = "private", metaScope = "private", factoryName = "of")
public final class LchMarginDetail extends MarginDetail implements ImmutableBean {

  /**
   * The total margin, expressed in the reporting currency.
   * This is the sum of the base margin and the add-ons.
   */
  @PropertyDefinition(validate = "notNull")
  private final double totalMargin;
  /**
   * The identifiers of the scenarios used to calculate margin for the diversified portfolio.
   * Use {@link #findScenario(String)} to retrieve the scenario details.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<String> baseScenarioIds;
  /**
   * The indices contained in the portfolio.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<LchMarginIndex> indices;
  /**
   * The scenarios used to calculate margin.
   * This consists only of those scenarios that were actively used in the portfolio
   * level diversified or the index level undiversified calculation.
   */
  @PropertyDefinition(validate = "notNull")
  private final List<LchMarginScenario> scenarios;

  //-------------------------------------------------------------------------
  @Override
  @DerivedProperty
  public Ccp getCcp() {
    return Ccp.LCH;
  }

  /**
   * Finds the first index matching by name.
   * 
   * @param indexName the index name
   * @return the optional index
   */
  public Optional<LchMarginIndex> findIndex(String indexName) {
    return indices.stream()
        .filter(val -> val.getIndexName().equalsIgnoreCase(indexName))
        .findFirst();
  }

  /**
   * Finds the first scenario matching by identifier.
   * 
   * @param scenarioId the scenario identifier
   * @return the optional scenario
   */
  public Optional<LchMarginScenario> findScenario(String scenarioId) {
    return scenarios.stream()
        .filter(val -> val.getId().equalsIgnoreCase(scenarioId))
        .findFirst();
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code LchMarginDetail}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return LchMarginDetail.Meta.INSTANCE;
  }

  static {
    MetaBean.register(LchMarginDetail.Meta.INSTANCE);
  }

  /**
   * Obtains an instance.
   * @param totalMargin  the value of the property, not null
   * @param baseScenarioIds  the value of the property, not null
   * @param indices  the value of the property, not null
   * @param scenarios  the value of the property, not null
   * @return the instance
   */
  public static LchMarginDetail of(
      double totalMargin,
      List<String> baseScenarioIds,
      List<LchMarginIndex> indices,
      List<LchMarginScenario> scenarios) {
    return new LchMarginDetail(
      totalMargin,
      baseScenarioIds,
      indices,
      scenarios);
  }

  private LchMarginDetail(
      double totalMargin,
      List<String> baseScenarioIds,
      List<LchMarginIndex> indices,
      List<LchMarginScenario> scenarios) {
    JodaBeanUtils.notNull(totalMargin, "totalMargin");
    JodaBeanUtils.notNull(baseScenarioIds, "baseScenarioIds");
    JodaBeanUtils.notNull(indices, "indices");
    JodaBeanUtils.notNull(scenarios, "scenarios");
    this.totalMargin = totalMargin;
    this.baseScenarioIds = Collections.unmodifiableList(new ArrayList<>(baseScenarioIds));
    this.indices = Collections.unmodifiableList(new ArrayList<>(indices));
    this.scenarios = Collections.unmodifiableList(new ArrayList<>(scenarios));
  }

  @Override
  public MetaBean metaBean() {
    return LchMarginDetail.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the total margin, expressed in the reporting currency.
   * This is the sum of the base margin and the add-ons.
   * @return the value of the property, not null
   */
  public double getTotalMargin() {
    return totalMargin;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the identifiers of the scenarios used to calculate margin for the diversified portfolio.
   * Use {@link #findScenario(String)} to retrieve the scenario details.
   * @return the value of the property, not null
   */
  public List<String> getBaseScenarioIds() {
    return baseScenarioIds;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the indices contained in the portfolio.
   * @return the value of the property, not null
   */
  public List<LchMarginIndex> getIndices() {
    return indices;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the scenarios used to calculate margin.
   * This consists only of those scenarios that were actively used in the portfolio
   * level diversified or the index level undiversified calculation.
   * @return the value of the property, not null
   */
  public List<LchMarginScenario> getScenarios() {
    return scenarios;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      LchMarginDetail other = (LchMarginDetail) obj;
      return JodaBeanUtils.equal(totalMargin, other.totalMargin) &&
          JodaBeanUtils.equal(baseScenarioIds, other.baseScenarioIds) &&
          JodaBeanUtils.equal(indices, other.indices) &&
          JodaBeanUtils.equal(scenarios, other.scenarios);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(totalMargin);
    hash = hash * 31 + JodaBeanUtils.hashCode(baseScenarioIds);
    hash = hash * 31 + JodaBeanUtils.hashCode(indices);
    hash = hash * 31 + JodaBeanUtils.hashCode(scenarios);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("LchMarginDetail{");
    buf.append("totalMargin").append('=').append(totalMargin).append(',').append(' ');
    buf.append("baseScenarioIds").append('=').append(baseScenarioIds).append(',').append(' ');
    buf.append("indices").append('=').append(indices).append(',').append(' ');
    buf.append("scenarios").append('=').append(JodaBeanUtils.toString(scenarios));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code LchMarginDetail}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code totalMargin} property.
     */
    private final MetaProperty<Double> totalMargin = DirectMetaProperty.ofImmutable(
        this, "totalMargin", LchMarginDetail.class, Double.TYPE);
    /**
     * The meta-property for the {@code baseScenarioIds} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<String>> baseScenarioIds = DirectMetaProperty.ofImmutable(
        this, "baseScenarioIds", LchMarginDetail.class, (Class) List.class);
    /**
     * The meta-property for the {@code indices} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<LchMarginIndex>> indices = DirectMetaProperty.ofImmutable(
        this, "indices", LchMarginDetail.class, (Class) List.class);
    /**
     * The meta-property for the {@code scenarios} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<LchMarginScenario>> scenarios = DirectMetaProperty.ofImmutable(
        this, "scenarios", LchMarginDetail.class, (Class) List.class);
    /**
     * The meta-property for the {@code ccp} property.
     */
    private final MetaProperty<Ccp> ccp = DirectMetaProperty.ofDerived(
        this, "ccp", LchMarginDetail.class, Ccp.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "totalMargin",
        "baseScenarioIds",
        "indices",
        "scenarios",
        "ccp");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -924857838:  // totalMargin
          return totalMargin;
        case -586668681:  // baseScenarioIds
          return baseScenarioIds;
        case 1943391143:  // indices
          return indices;
        case 1726545635:  // scenarios
          return scenarios;
        case 98320:  // ccp
          return ccp;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends LchMarginDetail> builder() {
      return new LchMarginDetail.Builder();
    }

    @Override
    public Class<? extends LchMarginDetail> beanType() {
      return LchMarginDetail.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -924857838:  // totalMargin
          return ((LchMarginDetail) bean).getTotalMargin();
        case -586668681:  // baseScenarioIds
          return ((LchMarginDetail) bean).getBaseScenarioIds();
        case 1943391143:  // indices
          return ((LchMarginDetail) bean).getIndices();
        case 1726545635:  // scenarios
          return ((LchMarginDetail) bean).getScenarios();
        case 98320:  // ccp
          return ((LchMarginDetail) bean).getCcp();
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
   * The bean-builder for {@code LchMarginDetail}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<LchMarginDetail> {

    private double totalMargin;
    private List<String> baseScenarioIds = Collections.emptyList();
    private List<LchMarginIndex> indices = Collections.emptyList();
    private List<LchMarginScenario> scenarios = Collections.emptyList();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -924857838:  // totalMargin
          return totalMargin;
        case -586668681:  // baseScenarioIds
          return baseScenarioIds;
        case 1943391143:  // indices
          return indices;
        case 1726545635:  // scenarios
          return scenarios;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -924857838:  // totalMargin
          this.totalMargin = (Double) newValue;
          break;
        case -586668681:  // baseScenarioIds
          this.baseScenarioIds = (List<String>) newValue;
          break;
        case 1943391143:  // indices
          this.indices = (List<LchMarginIndex>) newValue;
          break;
        case 1726545635:  // scenarios
          this.scenarios = (List<LchMarginScenario>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public LchMarginDetail build() {
      return new LchMarginDetail(
          totalMargin,
          baseScenarioIds,
          indices,
          scenarios);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(160);
      buf.append("LchMarginDetail.Builder{");
      buf.append("totalMargin").append('=').append(JodaBeanUtils.toString(totalMargin)).append(',').append(' ');
      buf.append("baseScenarioIds").append('=').append(JodaBeanUtils.toString(baseScenarioIds)).append(',').append(' ');
      buf.append("indices").append('=').append(JodaBeanUtils.toString(indices)).append(',').append(' ');
      buf.append("scenarios").append('=').append(JodaBeanUtils.toString(scenarios));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
