/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sdk.margin;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.beans.Bean;
import org.joda.beans.ser.JodaBeanSer;
import org.testng.annotations.Test;

/**
 * Test {@link PortfolioDataFile}.
 */
@Test
@SuppressWarnings("deprecation")
public class PortfolioDataFileTest {

  public void test_ofString_small() {
    PortfolioDataFile test = PortfolioDataFile.of("name.txt", "a=b");
    assertEquals(test.getName(), "name.txt.gz.base64");
    assertEquals(test.getData(), "H4sIAAAAAAAAAEu0TQIAzzAAfwMAAAA=");
  }

  public void test_ofString_large() {
    Random random = new Random(1);
    StringBuilder buf = new StringBuilder(1_200_000);
    for (int i = 0; i < 1_200_000; i++) {
      buf.append(random.nextInt(64) + 32);
    }
    String str = buf.toString();
    PortfolioDataFile test = PortfolioDataFile.of("name.txt", str);
    assertEquals(test.getName(), "name.txt.gz.base64");
    assertEquals(test.getData(), Base64.getEncoder().encodeToString(gzip(str)));
  }

  //-------------------------------------------------------------------------
  public void test_ofPath_CSV() {
    Path path = Paths.get("src/test/resources/simple.csv");
    PortfolioDataFile test = PortfolioDataFile.of(path);
    assertEquals(test.getName().endsWith("simple.csv.gz.base64"), true);
    assertEquals(test.getData(), Base64.getEncoder().encodeToString(gzip(path)));
  }

  public void test_ofPath_XML() {
    Path path = Paths.get("src/test/resources/simple.xml");
    PortfolioDataFile test = PortfolioDataFile.of(path);
    assertEquals(test.getName().endsWith("simple.xml.gz.base64"), true);
    assertEquals(test.getData(), Base64.getEncoder().encodeToString(gzip(path)));
  }

  public void test_ofPath_XLS() {
    Path path = Paths.get("src/test/resources/simple.xls");
    PortfolioDataFile test = PortfolioDataFile.of(path);
    assertEquals(test.getName().endsWith("simple.xls.gz.base64"), true);
    assertEquals(test.getData(), Base64.getEncoder().encodeToString(gzip(path)));
  }

  public void test_ofPath_XLSX() {
    Path path = Paths.get("src/test/resources/simple.xlsx");
    PortfolioDataFile test = PortfolioDataFile.of(path);
    assertEquals(test.getName().endsWith("simple.xlsx.gz.base64"), true);
    assertEquals(test.getData(), Base64.getEncoder().encodeToString(gzip(path)));
  }

  //-------------------------------------------------------------------------
  public void test_ofBean_unchanged() {
    Bean bean = PortfolioDataFile.of("name.txt", "a=b");
    PortfolioDataFile test = PortfolioDataFile.of(bean);
    assertSame(test, bean);
  }

  public void test_ofBean_trade() {
    Bean bean = TradeValue.of(1, "GBP", 2);
    String xml = JodaBeanSer.COMPACT.xmlWriter().write(bean);
    PortfolioDataFile test = PortfolioDataFile.of(bean);
    assertEquals(test, PortfolioDataFile.of("TradeValue.xml", xml));
  }

  //-------------------------------------------------------------------------
  public void test_ofCombined() {
    Path path1 = Paths.get("src/test/resources/simple.xml");
    Path path2 = Paths.get("src/test/resources/simple.xls");
    PortfolioDataFile test = PortfolioDataFile.ofCombined(Arrays.asList(path1, path2));
    assertEquals(test.getName(), "JavaSDK.zip.base64");
    assertEquals(test.getData(), Base64.getEncoder().encodeToString(zip(path1, path2)));
  }

  private static byte[] gzip(String str) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (GZIPOutputStream zos = new GZIPOutputStream(baos)) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        zos.write(bytes);
      }
      return baos.toByteArray();
    } catch (IOException ex) {
      throw new RuntimeException("Failed to zip content", ex);
    }
  }

  private static byte[] gzip(Path path) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (GZIPOutputStream zos = new GZIPOutputStream(baos)) {
        Files.copy(path, zos);
      }
      return baos.toByteArray();
    } catch (IOException ex) {
      throw new RuntimeException("Failed to zip content", ex);
    }
  }

  private static byte[] zip(Path path1, Path path2) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (ZipOutputStream zos = new ZipOutputStream(baos)) {
        ZipEntry entry1 = new ZipEntry(path1.getFileName().toString());
        zos.putNextEntry(entry1);
        Files.copy(path1, zos);
        zos.closeEntry();
        ZipEntry entry2 = new ZipEntry(path2.getFileName().toString());
        zos.putNextEntry(entry2);
        Files.copy(path2, zos);
        zos.closeEntry();
      }
      return baos.toByteArray();
    } catch (IOException ex) {
      throw new RuntimeException("Failed to zip content", ex);
    }
  }

}
