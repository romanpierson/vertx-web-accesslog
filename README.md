# vertx-web-accesslog

An access log implementation to be used in vert web routes.

Inspired and with intention to be compliant with

* Apache HTTP Server mod_log_config module

* W3C Extended Log File Format (http://www.w3.org/TR/WD-logfile.html)

## Access Log Pattern Configuration

The logger supports mixing of both log formats and is also designed to easily add custom log elements

## Logging framework

Generating the access log files is performed in a transparent way by vertx logger. Therefore there is any restriction regarding the logging framework used behind (however logback is recommended). Defining rollover strategies (size, daily, etc) are dealt with by the logging framework as well.

## Conditional Log generation (Channels) - Future Improvement

The idea is to support certain kind of conditional log generation, eg to allow to generate different access log patterns for different request patterns, response statuses, etc

## Usage

### Configure route

Just put an instance of AccessLogHandler as first route handler

```java
Router router = Router.router(vertx);

router
	.route()
		.handler(AccessLoggerHandler.create("\"cs-uri\" cs-method %s %D %T" ));
```

### Configure Logger

The logger itself in the current solution does not has a built in mechanism to write the the physical access file. Instead this is done by the logging framework used behind. 

To chose to which logging implementation vertx logger should delegate you need to set property `vertx.logger-delegate-factory-class-name`, eg like this

```java
System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
```

When defining the logger in the implementation used you need to make sure it refers to `com.mdac.vertx.web.accesslogger.impl.AccessLoggerHandlerImpl`


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
Incoming Headers | %{IDENTIFIER}i  | - | If not found - will be logged |
Outgoing Response Headers | %{IDENTIFIER}o  | - | If not found - will be logged |
Cookie | %{IDENTIFIER}c  | - | If not found - will be logged |





