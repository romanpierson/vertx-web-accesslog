Generating the access log files is performed in a transparent way by vertx logger. Therefore there is any restriction regarding the logging framework used behind (however logback is recommended and test case is implemented using logback / SLF4J). Main reason for this is to not have a dependency on a specific logging framework and also to ensure that implementation details like for example rollover strategies (size, daily, etc) are dealt with by the logging framework.


# Usage

## Appender Configuration

As configuration is now done by plain JsonObject its very simple to use and inject configuration eg by yaml, see as an example:

```yaml
configurations:
  - identifier: accesslog-formatted
    logPattern: '%{}t %D "cs-uri"'
    appenders:
      - appenderClassName : com.romanpierson.vertx.web.accesslogger.appender.logging.impl.LoggingAppender
        config:
          loggerName: accesslog
```


## Configure Logger

The logger itself in the current solution does not has a built in mechanism to write to the physical access file. Instead this is done by the logging framework used behind.  

### Logback Configuration

This shows how eg you define with logback.xml - as you see the loggerName has to match with what is defined in the appender configuration

```xml
<configuration>
  
  <appender name="ACCESS_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
  	<file>${access.location}/access.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
    	<fileNamePattern>access.%d{yyyy-MM-dd}.log</fileNamePattern>
		<maxHistory>10</maxHistory>
    </rollingPolicy>
    <encoder>
     	<pattern>%msg%n</pattern>
    	<immediateFlush>true</immediateFlush>
    </encoder>
  </appender>
  
  
	<appender name="ACCESS_FILE_ASYNC" class="ch.qos.logback.classic.AsyncAppender">
  		<discardingThreshold>0</discardingThreshold>
  		<queueSize>500</queueSize>
  		<maxFlushTime>5000</maxFlushTime>
 		<appender-ref ref="ACCESS_FILE" />
 	</appender>
  
  

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender" level="info">
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
  
  <logger name="accesslog" level="info" additivity="false">
      <appender-ref ref="ACCESS_FILE" />
    </logger>
    
  <root level="info">
    <appender-ref ref="STDOUT" />
  </root>
  
</configuration>
```

If you use a dynamic part like the root log folder you have make sure it is set like this when you create the vertx instance byself:

```java
System.setProperty("access.location", "/tmp/accesslog ");

// Start vertx here...

```

If you are using a fatjar it needs to be like this (Be aware that the properties have to go before the jar in order to get picked up):

```java
java \
-Daccess.location=/Users/x/y/logs \
-jar myFatJar.jar 
```
