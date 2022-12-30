
# Usage

## Appender configuration

As configuration is now done by plain JsonObject its very simple to use and inject configuration eg by yaml, see as an example:

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

## Conventions

By default all log elements configured will be sent to the indexer to be interpreted as message to be indexed. However the timestamp is passed to the indexer as meta data as the indexer potentially requires that raw value eg to determinate the target index name. 
By default you should put as first element plain timestamp %{msec} and the appender will remove that element from the message sent to the indexer.  

The instance identifier tells the indexer verticle to what ES instance the data should be indexed. All the detailed configuration of this is done directly on the indexer verticle itself (so the appender does not have to know about this).

## Field Names Definition

As for each field to be indexed a specific name needs to be used this has to be explicitly set by configuration property `fieldNames`. Be aware that you need to put a name for all fields of your logpattern even for eg the timestamp one that is actually skipped at the end. The real fieldname used in the indexer for the ES timestamp field is configured in the indexer verticle itself.
