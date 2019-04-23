# OpenGamma Java SDK

[![Build Status](https://travis-ci.org/OpenGamma/JavaSDK.svg?branch=master)](https://travis-ci.org/OpenGamma/JavaSDK) [![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The OpenGamma service is a platform for the independent calculation of metrics such as margin, fees and balance sheet,
to enable firms to optimize execution and clearing of derivatives.
The service is available commercially from [OpenGamma](http://www.opengamma.com/).

This repository contains the source code of the SDK of the OpenGamma service.
It is released as Open Source Software under the [Apache v2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html). 
The SDK is only provided in Java, however the service itself can be called from any language.

[![OpenGamma](https://s3-eu-west-1.amazonaws.com/og-public-downloads/og-logo-alpha.png "OpenGamma")](http://www.opengamma.com)


Using this library
------------------

Java SE 8u40 or later is required to use the SDK.
The JAR files are available in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.opengamma.sdk%22).
For example, to access the SDK for the margin service, use:

```
<dependency>
  <groupId>com.opengamma.sdk</groupId>
  <artifactId>sdk-margin</artifactId>
  <version>3.5.0</version>
</dependency>
```

## Releases

See the [change log](CHANGELOG.md).
