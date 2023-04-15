package com.romanpierson.vertx.web.accesslogger;


import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import com.romanpierson.vertx.test.verticle.SimpleJsonResponseVerticle;
import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.ElasticSearchAppenderConfig;
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
	@Order(value = 1)
	void testValid(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			testContext.failNow(throwable);
		});
		
		vertx.eventBus().<JsonArray>consumer(ElasticSearchAppenderConfig.ELASTICSEARCH_INDEXER_EVENTBUS_EVENT_NAME, message -> {
			//assertEquals(1, message.body().size());
			//assertEquals("/test", message.body().getString(0));
			System.out.println(message.body());
			testContext.completeNow();
		});
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-elasticsearch-appender-valid.yaml"), testContext.succeeding(id2 -> {
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
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
