## Changelog

### 2.7.0 (Vert.x 5)

(2025-05-15)

* Functional identical version of `1.7.0` for `vert.x 5`


### 1.7.0

(2024-01-13)

* Native handling of values
* All Log Elements return now native objects if possible
* String based Appenders have to stringify themselves
* Appenders that understand native objects benefit from this change
* **BREAKING CHANGE**: `Appender.push` signature changed and simplified - only relevant if you have a custom Appender
* Upgrade to Vert.x 4.5.1 -> Ready for Virtual Threads
* Automatic injection of `timestamp` for `ElasticSearchAppender` - no need anymore to define it artifically in logPattern

### 1.6.1

(2023-11-10)

* Headers case insensitive
* Upgrade to latest versions

### 1.6.0

(2023-04-19)

* Refactored and simplified PatternResolver and Element code using java functions
* Raised test coverage
* Allow easy overwriting of existing patterns with custom elements
* Upgrade to latest versions

### 1.5.0

(2022-12-30)

* Moved package `com.mdac` to `com.romanpierson` (for maven central)
* Upgrade to latest versions
* Moved from Travis CI to Github Actions / Gradle Sonarqube Plugin
* Removed slf4j dependency
* Integrated `LoggingAppender` and `ElasticSearchAppender` into core library

### 1.4.0

(2020-12-17)

* Upgrade to Vertx 4, Junit 5, Gradle, all libraries latest versions
* Added `EnvironmentValueElement`

### 1.3.1

(2019-04-17)

* Fixed a bug with pattern resolver (https://github.com/romanpierson/vertx-web-accesslog/issues/11)

### 1.3.0

(2019-02-14)

* Changed configuration from custom Option classes to plain JsonObject
* Added plain timestamp as log element
* Replaced `PrintStreamAppender` with `ConsoleAppender`
* Added `EventBusAppender`
* Fixed a bug with picking up the best log element if multiple ones potentially fit
* Added `StaticValueElement`

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
