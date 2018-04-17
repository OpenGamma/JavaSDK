# OpenGamma Java SDK

[![Build Status](https://travis-ci.org/OpenGamma/JavaSDK.svg?branch=master)](https://travis-ci.org/OpenGamma/JavaSDK) [![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The OpenGamma service is a platform for the independent calculation of metrics such as margin, fees and balance sheet,
to enable firms to optimize execution and clearing of derivatives.
The service is available commercially from [OpenGamma](http://www.opengamma.com/).

This repository contains the source code of the SDK of the OpenGamma service.
It is released as Open Source Software under the [Apache v2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html). 
The SDK is only provided in Java, however the service itself can be called from any language.

[![OpenGamma](http://developers.opengamma.com/res/display/default/chrome/masthead_logo.png "OpenGamma")](http://www.opengamma.com)


Using this library
------------------

Java SE 8u40 or later is required to use the SDK.
The JAR files are available in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.opengamma.sdk%22).
For example, to access the SDK for the margin service, use:

```
<dependency>
  <groupId>com.opengamma.sdk</groupId>
  <artifactId>sdk-margin</artifactId>
  <version>3.0.1</version>
</dependency>
```

v3.0.1
------
The asynchronous margin calculation methods have been rewritten.

v3.0.0
------
The SDK packages are restructured to a design suitable for the long term.
