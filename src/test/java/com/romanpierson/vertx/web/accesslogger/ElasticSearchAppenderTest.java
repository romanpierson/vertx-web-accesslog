package com.romanpierson.vertx.web.accesslogger;


import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import com.romanpierson.vertx.test.verticle.SimpleJsonResponseVerticle;
import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.ElasticSearchAppenderConfig;
import com.romanpierson.vertx.web.accesslogger.exception.AccessLoggerException;
import com.romanpierson.vertx.web.accesslogger.verticle.AccessLoggerProducerVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class ElasticSearchAppenderTest {

	@Test
	void testInvalidElasticSearchAppenderWithMissingInstanceIdentifier(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			assertTrue(throwable instanceof AccessLoggerException);
			assertEquals("Failed to create appender with [com.romanpierson.vertx.web.accesslogger.appender.elasticsearch.impl.ElasticSearchAppender]", throwable.getMessage());
			Throwable internalCause = throwable.getCause().getCause();
			assertTrue(internalCause instanceof IllegalArgumentException);
			assertEquals("instanceIdentifier must not be empty", internalCause.getMessage());
			testContext.completeNow();
		});
		
		vertx
			.deployVerticle(new AccessLoggerProducerVerticle())
			.onComplete(testContext.succeeding(deploymentId -> {
				vertx
					.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-invalid-console-appender.yaml"))
					.onComplete(testContext.succeedingThenComplete());
			}));
	}
	
	@Test
	void testInvalidElasticSearchAppenderWithMissingFieldNames(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			assertTrue(throwable instanceof AccessLoggerException);
			assertEquals("Failed to create appender with [com.romanpierson.vertx.web.accesslogger.appender.elasticsearch.impl.ElasticSearchAppender]", throwable.getMessage());
			Throwable internalCause = throwable.getCause().getCause();
			assertTrue(internalCause instanceof IllegalArgumentException);
			assertEquals("fieldNames must not be empty", internalCause.getMessage());
			testContext.completeNow();
		});
		
		vertx
			.deployVerticle(new AccessLoggerProducerVerticle())
			.onComplete(testContext.succeeding(deploymentId -> {
				vertx
					.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-elasticsearch-appender-invalid-fieldnames.yaml"))
					.onComplete(testContext.succeedingThenComplete());
			}));
	}
	
	@Test
	void testValid(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			testContext.failNow(throwable);
		});
		
		vertx.eventBus().<JsonArray>consumer(ElasticSearchAppenderConfig.ELASTICSEARCH_INDEXER_EVENTBUS_EVENT_NAME, message -> {
			//assertEquals(1, message.body().size());
			//assertEquals("/test", message.body().getString(0));
			System.out.println(message.body());
			// TODO verify content
			testContext.completeNow();
		});
		
		vertx
			.deployVerticle(new AccessLoggerProducerVerticle())
				.onComplete(testContext.succeeding(deploymentId -> {
					vertx
						.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-elasticsearch-appender-valid.yaml"))
						.onComplete(testContext.succeeding(deploymentId2 -> {
							
							// Just to fix github actions issue
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// we dont care
							}
							
							HttpClient client = vertx.createHttpClient();
							
							client
								.request(HttpMethod.GET, 8080, "localhost", "/test")
								.compose(req -> req.send().compose(HttpClientResponse::body))
								.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
										
								})));
							
						}));
				}));
		
	}
}
