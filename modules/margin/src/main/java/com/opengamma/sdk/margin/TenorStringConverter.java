/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.time.Period;

import org.joda.convert.TypedStringConverter;

/**
 * Converts tenors, not prefixed by 'P', to periods.
 */
class TenorStringConverter implements TypedStringConverter<Period> {

  @Override
  public String convertToString(Period object) {
    return object.toString();
  }

  @Override
  public Period convertFromString(Class<? extends Period> cls, String str) {
    return str.startsWith("P") ? Period.parse(str) : Period.parse("P" + str);
  }

  @Override
  public Class<?> getEffectiveType() {
    return Period.class;
  }

}
