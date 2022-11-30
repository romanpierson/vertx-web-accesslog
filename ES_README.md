## THIS PROJECT IS DEPRECATED AND DISCONTINUED

Its functionality is integrated now as part of [vertx-web-accesslog](https://github.com/romanpierson/vertx-web-accesslog)

# vertx-web-accesslog-elasticsearch-appender

An appender implementation to be used with [vertx-web-accesslog](https://github.com/romanpierson/vertx-web-accesslog).

Writes its data to a [vertx-elasticsearch-indexer](https://github.com/romanpierson/vertx-elasticsearch-indexer) instance. For details check out the documentation there.

## Technical Usage

The artefact is published on bintray / jcenter (https://bintray.com/romanpierson/maven/com.mdac.vertx-web-accesslog-elasticsearch-appender)

Just add it as a dependency to your project (gradle example)

```xml
dependencies {
	compile 'com.mdac:vertx-web-accesslog-elasticsearch-appender:1.4.0'
}
```

## Usage

### Configure route

```java
Router router = Router.router(vertx);

JsonObject config = .... load or create your configuration json

router.route().handler(AccessLoggerHandler.create(config));

```

As configuration is now done by plain JsonObject its very simple to use and inject configuration eg by yaml, see as an example `ServerSetupStarter`

```yaml
configurations:
  - identifier: accesslog-plain
    logPattern: "%{msec}t %D cs-uri"
    appenders:
      - appenderClassName : com.mdac.vertx.web.accesslogger.appender.elasticsearch.impl.ElasticSearchAppender
        config:
          instanceIdentifier: accesslog
          fieldNames:
            - timestamp
            - duration
            - uri
```

### Conventions

By default all log elements configured will be sent to the indexer to be interpreted as message to be indexed. However the timestamp is passed to the indexer as meta data as the indexer potentially requires that raw value eg to determinate the target index name. 
By default you should put as first element plain timestamp %{msec} and the appender will remove that element from the message sent to the indexer.  

The instance identifier tells the indexer verticle to what ES instance the data should be indexed. All the detailed configuration of this is done directly on the indexer verticle itself (so the appender does not have to know about this).

### Field Names Definition

As for each field to be indexed a specific name needs to be used this has to be explicitly set by configuration property `fieldNames`. Be aware that you need to put a name for all fields of your logpattern even for eg the timestamp one that is actually skipped at the end. The real fieldname used in the indexer for the ES timestamp field is configured in the indexer verticle itself.

## Changelog

### 1.3.0 (2019-02-14)

* Initial version

### 1.4.0 (2020-12-24)

* Upgrade to Vertx 4
