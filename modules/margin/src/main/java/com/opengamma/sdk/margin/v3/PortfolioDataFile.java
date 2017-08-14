/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin.v3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

/**
 * Portfolio data to pass to the service.
 */
@BeanDefinition(builderScope = "private", metaScope = "private")
public final class PortfolioDataFile implements ImmutableBean {

  /**
   * The portfolio name.
   */
  @PropertyDefinition(validate = "notNull")
  private final String name;
  /**
   * The portfolio data.
   */
  @PropertyDefinition(validate = "notNull")
  private final String data;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance from the String representation of the data.
   * <p>
   * The data is compressed using GZIP, and sent to the server using Base-64.
   *
   * @param name the name, not empty
   * @param data the data, not empty
   * @return the instance
   * @throws UncheckedIOException if an IO error occurs
   */
  public static PortfolioDataFile of(String name, String data) {
    String base64Data = gzipBase64(data);
    return new PortfolioDataFile(name + ".gz.base64", base64Data);
  }

  /**
   * Obtains an instance from a file.
   * <p>
   * The file is compressed using GZIP, and sent to the server using Base-64.
   *
   * @param path the file
   * @return the instance
   * @throws UncheckedIOException if an IO error occurs
   */
  public static PortfolioDataFile of(Path path) {
    String filename = path.toAbsolutePath().toString();
    if (Files.notExists(path)) {
      throw new IllegalArgumentException("Could not find portfolio file: " + filename);
    }
    String base64Data = gzipBase64(path);
    return new PortfolioDataFile(filename + ".gz.base64", base64Data);
  }

  /**
   * Obtains an instance by combining a list of files.
   * <p>
   * The files are combined using ZIP, and sent to the server using Base-64.
   *
   * @param paths the files, at least one
   * @return the instance
   * @throws IllegalArgumentException if no files were passed in
   * @throws UncheckedIOException if an IO error occurs
   */
  public static PortfolioDataFile ofCombined(List<Path> paths) {
    List<Path> missingFiles = paths.stream().filter(Files::notExists).collect(Collectors.toList());
    if (!missingFiles.isEmpty()) {
      String missingFilesAsString = missingFiles.stream().map(Path::toString).collect(Collectors.joining(","));
      throw new IllegalArgumentException("Could not find one or more of the input files in the list." + missingFilesAsString);
    }
    String base64Data = zipBase64(paths);
    return new PortfolioDataFile("JavaSDK.zip.base64", base64Data);
  }

  /**
   * Convert input to bytes using UTF-8, gzip it, then base-64 it.
   *
   * @param data the input data, as a String
   * @return the compressed output, as a String
   */
  private static String gzipBase64(String data) {
    try {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length() / 4 + 1)) {
        try (OutputStream baseos = Base64.getEncoder().wrap(baos)) {
          try (GZIPOutputStream zos = new GZIPOutputStream(baseos)) {
            try (OutputStreamWriter writer = new OutputStreamWriter(zos, StandardCharsets.UTF_8)) {
              writer.write(data);
            }
          }
        }
        return baos.toString("ISO-8859-1");  // base-64 bytes are ASCII, so this is optimal
      }
    } catch (IOException ex) {
      throw new UncheckedIOException("Failed to gzip base-64 content", ex);
    }
  }

  /**
   * Gzips the input then base-64 it.
   *
   * @param path the input data file, as an instance of {@link Path}
   * @return the compressed output, as a String
   */
  private static String gzipBase64(Path path) {
    try {
      long size = Files.size(path) / 4 + 1;
      int initialSize = size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size;
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream(initialSize)) {
        try (OutputStream baseos = Base64.getEncoder().wrap(baos)) {
          try (GZIPOutputStream zos = new GZIPOutputStream(baseos)) {
            Files.copy(path, zos);
          }
        }
        return baos.toString("ISO-8859-1");  // base-64 bytes are ASCII, so this is optimal
      }
    } catch (IOException ex) {
      throw new UncheckedIOException("Failed to gzip base-64 content", ex);
    }
  }

  /**
   * Combines multiple files into a ZIP archive, then base-64 the ZIP archive.
   *
   * @param paths a list of one or more input files, that are to be compressed together
   * @return the compressed output, as a String
   */
  private static String zipBase64(List<Path> paths) {
    if (paths.isEmpty()) {
      throw new IllegalArgumentException("PortfolioDataFile requires at least one file");
    }
    try {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 8)) {
        try (OutputStream baseos = Base64.getEncoder().wrap(baos)) {
          try (ZipOutputStream zos = new ZipOutputStream(baseos)) {
            for (Path path : paths) {
              ZipEntry entry = new ZipEntry(path.getFileName().toString());
              zos.putNextEntry(entry);
              Files.copy(path, zos);
              zos.closeEntry();
            }
          }
        }
        return baos.toString("ISO-8859-1");  // base-64 bytes are ASCII, so this is optimal
      }
    } catch (IOException ex) {
      throw new UncheckedIOException("Failed to zip base-64 content", ex);
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code PortfolioDataFile}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return PortfolioDataFile.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(PortfolioDataFile.Meta.INSTANCE);
  }

  private PortfolioDataFile(
      String name,
      String data) {
    JodaBeanUtils.notNull(name, "name");
    JodaBeanUtils.notNull(data, "data");
    this.name = name;
    this.data = data;
  }

  @Override
  public MetaBean metaBean() {
    return PortfolioDataFile.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio name.
   * @return the value of the property, not null
   */
  public String getName() {
    return name;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio data.
   * @return the value of the property, not null
   */
  public String getData() {
    return data;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      PortfolioDataFile other = (PortfolioDataFile) obj;
      return JodaBeanUtils.equal(name, other.name) &&
          JodaBeanUtils.equal(data, other.data);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(name);
    hash = hash * 31 + JodaBeanUtils.hashCode(data);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("PortfolioDataFile{");
    buf.append("name").append('=').append(name).append(',').append(' ');
    buf.append("data").append('=').append(JodaBeanUtils.toString(data));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code PortfolioDataFile}.
   */
  private static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> name = DirectMetaProperty.ofImmutable(
        this, "name", PortfolioDataFile.class, String.class);
    /**
     * The meta-property for the {@code data} property.
     */
    private final MetaProperty<String> data = DirectMetaProperty.ofImmutable(
        this, "data", PortfolioDataFile.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "name",
        "data");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case 3076010:  // data
          return data;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends PortfolioDataFile> builder() {
      return new PortfolioDataFile.Builder();
    }

    @Override
    public Class<? extends PortfolioDataFile> beanType() {
      return PortfolioDataFile.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((PortfolioDataFile) bean).getName();
        case 3076010:  // data
          return ((PortfolioDataFile) bean).getData();
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
   * The bean-builder for {@code PortfolioDataFile}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<PortfolioDataFile> {

    private String name;
    private String data;

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(meta());
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case 3076010:  // data
          return data;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          this.name = (String) newValue;
          break;
        case 3076010:  // data
          this.data = (String) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public PortfolioDataFile build() {
      return new PortfolioDataFile(
          name,
          data);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("PortfolioDataFile.Builder{");
      buf.append("name").append('=').append(JodaBeanUtils.toString(name)).append(',').append(' ');
      buf.append("data").append('=').append(JodaBeanUtils.toString(data));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
