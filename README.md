[![Actions Status](https://github.com/romanpierson/vertx-web-accesslog/workflows/CI/badge.svg)](https://github.com/romanpierson/vertx-web-accesslog/actions)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=romanpierson_vertx-web-accesslog&metric=coverage)](https://sonarcloud.io/dashboard?id=romanpierson_vertx-web-accesslog)
![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)

# vertx-web-accesslog

An access log implementation to be used in vert web routes.

Inspired and with intention to be compliant with

* Apache HTTP Server mod_log_config module (http://httpd.apache.org/docs/2.4/en/mod/mod_log_config.html)

* W3C Extended Log File Format (http://www.w3.org/TR/WD-logfile.html)

## Technical Usage

The artefact is published on bintray / jcenter (https://bintray.com/romanpierson/maven/com.mdac.vertx-web-accesslog)

Just add it as a dependency to your project (gradle example)

```xml
dependencies {
	compile 'com.mdac:vertx-web-accesslog:1.4.0_RC1'
}
```

### Compatibility with Vertx core

Accesslog version | Vertx version
----|------
1.4.0 | 4.0.0
1.3.1 | 3.3.0 - 3.7.0
1.2.0 | 3.3.0 - 3.7.0

Previous versions of Vertx 3 could be supported with small adaptations, most caused by changes in the vertx-web API.


## Access Log Pattern Configuration

The logger supports mixing of both log formats and is also designed to easily add custom log elements

### Define your custom AccessLogElement

You can easily create your custom implementation of `AccessLogElement` by creating your own element class implementing `AccessLogElement` interface. The available `AccessLogElement` types are discovered by ServiceLoader, so just add to your resources a file like this and inside list your `AccessLogElement` classes

```xml
META-INF
 services
  com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement
}
```

## Appenders

### Available Appenders

Appender | Description
----|------
Console Appender | Embedded - main purpose for testing
EventBus Appender | Embedded - simple way to forward access events to a configurable address on the event bus
[Logging Appender] | Embedded - Using common logging functionality (logback, slf4j, etc)
[ElasticSearch Appender](https://github.com/romanpierson/vertx-web-accesslog-elasticsearch-appender) | Experimental appender that writes data to ES

### Custom Appenders

You can easily write your own appender doing 

* Write your own CustomAppender class that must
** implement `Appender` Interface
** have a public constructor taking a `JsonObject` instance holding the configuration

## AccessLoggerProducerVerticle

This verticle is responsible for receiving the raw data, formatting it based on the AccessLogElements configured and forwards the resulting data to the registered Appenders (by calling `Appender.push`). 

There is one worker instance of `AccessLoggerProducerVerticle` per vertx context started if you put configuration value `isAutoDeployProducerVerticle` to `true` (by default it is). If you prefer to manage the deployment of that verticle byself set the property to `false`.

## Usage

### Configure route

Just put an instance of AccessLogHandler as first route handler.

```java
Router router = Router.router(vertx);

JsonObject config = .... load or create your configuration json

router.route().handler(AccessLoggerHandler.create(config));

```

As configuration is now done by plain JsonObject its very simple to use and inject configuration eg by yaml, see as an example `ServerSetupStarter`

```yaml
configurations:
  - identifier: accesslog-formatted
    logPattern: '%{}t %D "cs-uri"'
    appenders:
      - appenderClassName : com.mdac.vertx.web.accesslogger.appender.console.impl.ConsoleAppender
  - identifier: accesslog-plain
    logPattern: "%{msec}t %D cs-uri"
    appenders:
      - appenderClassName : com.mdac.vertx.web.accesslogger.appender.console.impl.ConsoleAppender
```

## Supported log elements

Currently those elements are supported

Element | Apache | W3C | Remarks
----|------|------------| --------
Method | %m  | cs-method | |
Status | %s  | sc-status | |
Duration s | %T  | - |  |
Duration ms | %D  | - | |
Remote Host | %h  | - |  |
Local Host | %v  | - |  |
Local port | %p  | - |  |
Bytes Written v1 | %B | - | Zero Bytes written as 0 |
Bytes Written v2 | %b | - | Zero Bytes written as - |
First line of request | %r  | - | |
URI path only | %U | cs-uri-stem | |
Query only | %q | cs-uri-query | |
URI path incl query | - | cs-uri | |
Version / Protocol | %H | - | |
Datetime Apache | %t | - | Logs by default the request timestamp using format 'EEE, dd MMM yyyy HH:mm:ss zzz', Locale English and Timezone GMT  |
Datetime Apache Timeunit | %t{msec} | - | Currently only milliseconds is supported  |
| Datetime Apache Configurable v1 | %{PATTERN}t | - | Specify the format pattern, by default it is used Locale English and Timezone GMT |
| Datetime Apache Configurable v2 | %{PATTERN\|TIMEZONE\|LOCALE}t | - | Specify format pattern, timezone and locale |
Incoming Headers | %{IDENTIFIER}i  | - |  |
Outgoing Response Headers | %{IDENTIFIER}o  | - |  |
Cookie | %{IDENTIFIER}C  | - |  |
Static value | %{IDENTIFIER}static  | - |  |

### Static values

For static values you should prefer to use the %{value}static element. In case you have an appender like `ConsoleAppender` or `LoggingAppender` that writes its output via the resolved pattern you can also put such static values directly into the logpattern as it will just stay as non resolved. However for other appenders like `ElasticSearchAppender` one you need to explicitly define the element.

### Empty behaviour

The default way for elements where no actual value can be evaluated is to return a `NULL` value. This way the appender is able to translate this into an empty string or eg skip the value if we index towards a solution like ElasticSearch.

## Changelog

### 1.2.0

(2019-01-10)

* Introduced Appender API and removed all except `PrintStreamAppender` to separate projects
* `AccessLogElerment` is able to claim what data it requires
* General refactoring into Constants
* Raw values are translated into formatted values and only those get passed to appenders
* Appenders do not get passed anymore `AccessLogElement` instances
* Fixed a bug in `DateTimeElement` that caused pattern definition not to work
* Fixed a bug in `findInRawPatternInternal` method of several `AccessLogElement` implementations that handle several element patterns and in the case of having a pattern with those following each other this might have led to the situation of bypassing the earlier one
* Extracting correct hostname for local host (%v)

### 1.3.0

(2019-02-14)

* Changed configuration from custom Option classes to plain JsonObject
* Added plain timestamp as log element
* Replaced `PrintStreamAppender` with `ConsoleAppender`
* Added `EventBusAppender`
* Fixed a bug with picking up the best log element if multiple ones potentially fit
* Added `StaticValueElement`

### 1.3.1

(2019-04-17)

* Fixed a bug with pattern resolver (https://github.com/romanpierson/vertx-web-accesslog/issues/11)

### 1.4.0

(2020-12-17)

* Upgrade to Vertx 4, JDK 11, Junit 5, Gradle, all libraries latest versions
* Added `EnvironmentValueElement`

### Next

(?)

* Upgrade to latest versions
* Moved from Travis CI to Github Actions / Gradle Sonarqube Plugin
* Removed slf4j dependency
* Integrated `LoggingAppender` into core library