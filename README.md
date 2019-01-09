[![Build Status](https://travis-ci.org/romanpierson/vertx-web-accesslog.svg?branch=master)](https://travis-ci.org/romanpierson/vertx-web-accesslog) ![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)

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
	compile 'com.mdac:vertx-web-accesslog:1.1.0'
}
```

### Compatibility with Vertx core

Accesslog version | Vertx version
----|------
1.1.0 | 3.2.0 - 3.6.2

Previous versions of Vertx 3 could be supported with small adaptations, most caused by changes in the vertx-web API.


## Access Log Pattern Configuration

The logger supports mixing of both log formats and is also designed to easily add custom log elements

### Define your custom AccessLogElement

You can easily create your custom implementation of `AccessLogElement` by creating your own element class implementing `AccessLogElement` interface. The available `AccessLogElement` types are discovered by ServiceLoeader, so just add to your resources a file like this and inside list your `AccessLogElement` classes

```xml
META-INF
 services
  com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement
}
```

## Appenders

The library comes with an embedded `PrintStreamAppender` and its main purpose is for testing.

You can implement your own `Appender` implementation or use one of those

Appender | Description
----|------
[Logging Appender](https://github.com/romanpierson/vertx-web-accesslog-logging-appender) | Using common logging functionality (logback, slf4j, etc)
ElasticSearch Appender (Kibana) | Coming soon .....


## Usage

### Configure route

Just put an instance of AccessLogHandler as first route handler (Using Logging Appender as example)

```java
Router router = Router.router(vertx);

router
	.route()
		.handler(AccessLoggerHandler.create(new AccessLoggerOptions().setPattern("%t %m %D %T"), 
			Arrays.asList(
				new LoggingAppenderOptions()
					.setLoggerName("com.mdac.vertx.web.accesslogger.impl.AccessLoggerHandlerImpl")
			)
		)
);
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
| Datetime Apache Configurable v1 | %{PATTERN}t | - | Specify the format pattern, by default it is used Locale English and Timezone GMT |
| Datetime Apache Configurable v2 | %{PATTERN\|TIMEZONE\|LOCALE}t | - | Specify format pattern, timezone and locale |
Incoming Headers | %{IDENTIFIER}i  | - |  |
Outgoing Response Headers | %{IDENTIFIER}o  | - |  |
Cookie | %{IDENTIFIER}C  | - |  |

### Empty behaviour

The default way for elements where no actual value can be evaluated is to return a `NULL` value and the appender is translating this into an empty string. 

## Changelog

### 1.1.0

* Introduced Appender API and removed all except `PrintStreamAppender` to separate projects
* `AccessLogElerment` is able to claim what data it requires
* General refactoring into Constants

### 1.2.0

* Raw values are translated into formatted values and only those get passed to appenders
* Appenders do not get passed anymore `AccessLogElement` instances
* Fixed a bug in `DateTimeElement` that caused pattern definition not to work
* Fixed a bug in `findInRawPatternInternal` method of several `AccessLogElement` implementations that handle several element patterns and in the case of having a pattern with those following each other this might have led to the situation of bypassing the earlier one