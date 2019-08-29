/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import java.util.Optional;

import org.joda.beans.MetaBean;
import org.joda.beans.ser.DefaultDeserializer;
import org.joda.beans.ser.SerDeserializer;
import org.joda.beans.ser.SerDeserializerProvider;

/**
 * Deserializes the result, handling the CCP specific nature of the response.
 */
final class MarginDetailDeserializer extends DefaultDeserializer implements SerDeserializerProvider {

  private static final String LCH = "LCH";
  
  /**
   * The type of the detail.
   */
  private final MetaBean detailMetaBean;

  // converts a CCP to a deserializer instance
  static Optional<MarginDetailDeserializer> of(Ccp ccp) {
    if (ccp.name().contains(LCH)) {
      // Any LCH variants use the LCH Margin Detail deserializer
      return Optional.of(new MarginDetailDeserializer(LchMarginDetail.meta()));
    } else {
      return Optional.empty();
    }
  }

  // constructor
  MarginDetailDeserializer(MetaBean detailMetaBean) {
    this.detailMetaBean = detailMetaBean;
  }

  //-------------------------------------------------------------------------
  @Override
  public SerDeserializer findDeserializer(Class<?> beanType) {
    return beanType == MarginDetail.class ? this : null;
  }

  @Override
  public MetaBean findMetaBean(Class<?> beanType) {
    return detailMetaBean;
  }

}
