[![Actions Status](https://github.com/romanpierson/vertx-web-accesslog/workflows/CI/badge.svg)](https://github.com/romanpierson/vertx-web-accesslog/actions)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=romanpierson_vertx-web-accesslog&metric=coverage)](https://sonarcloud.io/dashboard?id=romanpierson_vertx-web-accesslog)
[![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/vert-x3/vertx-awesome)

# vertx-web-accesslog

An access log implementation to be used in vertx web routes.

Inspired and with intention to be compliant with

* Apache HTTP Server mod_log_config module (http://httpd.apache.org/docs/2.4/en/mod/mod_log_config.html)

* W3C Extended Log File Format (http://www.w3.org/TR/WD-logfile.html)

The main idea is to have an event based logging, with the possibility (but not enforcing you to do so) to directly export a log event natively to a target system, and not having to first write and persist it to a file and then read and parse it again from there like eg ELK is doing it. The drawback that those kind of solutions have is performance but even more issues like recognizing complete stacktraces etc.
The ElasticSearch Appender is the only appender for now that takes advantage of those benefits.
However you are free of course to use a traditional ELK setup style and just write your output to either console or a specific logfile - and let eg logstash take over from there.

## Key facts

* Zero dependencies (apart of vertx-web obviously)
* Easily extendable and customizable
* Small memory footprint


## Technical Usage

The artefact is published on maven central.

Just add it as a dependency to your project (gradle example)

```xml
dependencies {
	compile 'com.romanpierson:vertx-web-accesslog:1.5.0'
}
```

## Compatibility with Vertx core

Accesslog version | Vertx version
----|------
1.5.0 | 4.2.0 >
1.4.0 | 4.0.0 - 4.1.x
1.3.1 | 3.3.0 - 3.7.0
1.2.0 | 3.3.0 - 3.7.0

Previous versions are listed for completeness only and not supported anymore.

## Access Log Pattern Configuration

The logger supports mixing of both log formats and is also designed to easily add custom log elements

### Define your custom AccessLogElement

You can easily create your custom implementation of `AccessLogElement` by creating your own element class implementing `AccessLogElement` interface. The available `AccessLogElement` types are discovered by ServiceLoader, so just add to your resources a file like this and inside list your `AccessLogElement` classes

```xml
META-INF
 services
  com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement
```

## Appenders

Appenders are basically a way to send the log data to one (or multiple) backends. This ships with a set of (hopefully) useful appenders but you can create your custom appenders in a very easy way.

### Available Appenders

Appender | Description
----|------
Console Appender | Embedded - main purpose for testing
EventBus Appender | Embedded - simple way to forward access events to a configurable address on the event bus
[Logging Appender](https://github.com/romanpierson/vertx-web-accesslog/blob/master/LA_README.md) | Embedded - Using common logging functionality (logback, slf4j, etc)
[ElasticSearch Appender](https://github.com/romanpierson/vertx-web-accesslog/blob/master/ES_README.md) | Embedded - Experimental appender that writes data to ElasticSearch (For usage eg in kibana)  Requires [Vertx ElasticSearch Indexer](https://github.com/romanpierson/vertx-elasticsearch-indexer)



### Custom Appenders

You can easily write your own appender doing 

Write your own CustomAppender class that must
* implement `Appender` Interface
* have a public constructor taking a `JsonObject` instance holding the configuration

## AccessLoggerProducerVerticle

This verticle is responsible for receiving the raw data, formatting it based on the AccessLogElements configured and forwards the resulting data to the registered Appenders (by calling `Appender.push`). 

There is one worker instance of `AccessLoggerProducerVerticle` per vertx context started if you put configuration value `isAutoDeployProducerVerticle` to `true` (by default it is). If you prefer to manage the deployment of that verticle byself set the property to `false`.

## Usage

This describes the basic usage. More specific info eg about the different appenders can be found on the links.

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
      - appenderClassName : com.romanpierson.vertx.web.accesslogger.appender.console.impl.ConsoleAppender
  - identifier: accesslog-plain
    logPattern: "%{msec}t %D cs-uri"
    appenders:
      - appenderClassName : com.romanpierson.vertx.web.accesslogger.appender.console.impl.ConsoleAppender
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
| Datetime Apache Configurable v2 | %{PATTERN\|TIMEZONE\|LANGUAGE}t | - | Specify format pattern, timezone and language |
Incoming Headers | %{IDENTIFIER}i  | - |  |
Outgoing Response Headers | %{IDENTIFIER}o  | - |  |
Cookie | %{IDENTIFIER}C  | - | Request cookies only |
Static value | %{IDENTIFIER}static  | - |  |
Environment Variable value | %{IDENTIFIER}env  | - |  |

### Static values

For static values you should prefer to use the %{value}static element. In case you have an appender like `ConsoleAppender` or `LoggingAppender` that writes its output via the resolved pattern you can also put such static values directly into the logpattern as it will just stay as non resolved. However for other appenders like `ElasticSearchAppender` one you need to explicitly define the element.

### Empty behavior

The default way for elements where no actual value can be evaluated is to return a `NULL` value. This way the appender is able to translate this into an empty string or eg skip the value if we index towards a solution like ElasticSearch.

## Changelog

Detailed changelog can be found [here](https://github.com/romanpierson/vertx-web-accesslog/blob/master/CHANGELOG.md).

## Demo Playground

A sample project that shows usage of this (and other related features) can be found [here](https://github.com/romanpierson/vertx-logging-playground).
