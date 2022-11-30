## THIS PROJECT IS DEPRECATED AND DISCONTINUED

Its functionality is integrated now as part of [vertx-web-accesslog](https://github.com/romanpierson/vertx-web-accesslog)

# vertx-web-accesslog-logging-appender

An appender implementation to be used with [vertx-web-accesslog](https://github.com/romanpierson/vertx-web-accesslog).

Generating the access log files is performed in a transparent way by vertx logger. Therefore there is any restriction regarding the logging framework used behind (however logback is recommended and test case is implemented using logback / SLF4J). Main reason for this is to not have a dependency on a specific logging framework and also to ensure that implementation details like for example rollover strategies (size, daily, etc) are dealt with by the logging framework.

## Technical Usage

The artefact is published on bintray / jcenter (https://bintray.com/romanpierson/maven/com.mdac.vertx-web-accesslog-logging-appender)

Just add it as a dependency to your project (gradle example)

```xml
dependencies {
	compile 'com.mdac:vertx-web-accesslog-logging-appender:1.4.0'
}
```

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
      - appenderClassName : com.mdac.vertx.web.accesslogger.appender.logging.impl.LoggingAppender
        config:
          loggerName: accesslog
```


### Configure Logger

The logger itself in the current solution does not has a built in mechanism to write to the physical access file. Instead this is done by the logging framework used behind.  

#### In the code before defining the access log handler

```java
System.setProperty("access.location", "/tmp/accesslog ");
```

#### Using a fatjar like this (Be aware that the properties have to go before the jar in order to get picked up):

```java
java \
-Daccess.location=/Users/x/y/logs \
-jar myFatJar.jar 
```

Make sure that the logger name defined on the appender corresponds with the one defined in the loggers implementation configuration.

For example see the different logging framework specific configuration files in `test.resources` directory and adapt the `build.gradle` file to use different log frameworks, by default logback version is active. 

## Changelog

### 1.2.0

(2019-01-10)

* Initial version (extracted from vertx-web-accesslog implementation)

### 1.4.0

(2020-12-24)

* Upgrade Vertx 4
