package com.mdac.vertx.web.accesslogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mdac.vertx.test.verticle.SimpleJsonResponseVerticle;
import com.mdac.vertx.web.accesslogger.exception.AccessLoggerException;
import com.mdac.vertx.web.accesslogger.verticle.AccessLoggerProducerVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class AcessLogServerTest {

	@Test
	@Order(value = 1)
	void testInvalidConfig(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			assertTrue(throwable instanceof IllegalArgumentException);
			assertEquals("must specify at least one valid configuration", throwable.getMessage());
			testContext.completeNow();
		});
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
				
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-invalid.yaml"));
				
		}));
	}
	
	@Test
	@Order(value = 1)
	void testInvalidConsoleAppenderWithMissingResolvedPatttern(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			throwable.printStackTrace();
			assertTrue(throwable instanceof AccessLoggerException);
			assertEquals("Failed to create appender with [com.mdac.vertx.web.accesslogger.appender.console.impl.ConsoleAppender]", throwable.getMessage());
			Throwable internalCause = throwable.getCause().getCause();
			assertTrue(internalCause instanceof IllegalArgumentException);
			assertEquals("resolvedPattern must not be empty", internalCause.getMessage());
			testContext.completeNow();
		});
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
				
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-invalid-console-appender.yaml"));
				
		}));
	}
	
	
	@Test
	@Order(value = 1)
	void testValidElementsToMemoryAppender(Vertx vertx, VertxTestContext testContext) {
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-all-valid-memory-appender.yaml"), testContext.succeeding(id2 -> {
				
				HttpClient client = vertx.createHttpClient();
				
				client
					.request(HttpMethod.GET, 8080, "localhost", "/test")
					.compose(req -> req.send().compose(HttpClientResponse::body))
					.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
							//assertThat(buffer.toString()).isEqualTo("Plop");
							System.out.println(buffer.toString());
							testContext.completeNow();
					})));
			}));
		}));
	}
	
	@Test
	@Order(value = 1)
	void testValidElementsToConsoleAppender(Vertx vertx, VertxTestContext testContext) {
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-all-valid-console-appender.yaml"), testContext.succeeding(id2 -> {
				HttpClient client = vertx.createHttpClient();
				
				client
					.request(HttpMethod.GET, 8080, "localhost", "/test")
					.compose(req -> req.send().compose(HttpClientResponse::body))
					.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
							//assertThat(buffer.toString()).isEqualTo("Plop");
							System.out.println(buffer.toString());
							testContext.completeNow();
					})));
			}));
		}));
	}
	
	@Test
	@Order(value = 100)
	void testAutocreationOfAppenderVerticle(Vertx vertx, VertxTestContext testContext) {
		
		vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-all-valid-console-appender-autocreation-verticle.yaml"), testContext.succeeding(id2 -> {
				HttpClient client = vertx.createHttpClient();
				
				client
					.request(HttpMethod.GET, 8080, "localhost", "/test")
					.compose(req -> req.send().compose(HttpClientResponse::body))
					.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
							//assertThat(buffer.toString()).isEqualTo("Plop");
							System.out.println(buffer.toString());
							testContext.completeNow();
					})));
			}));
	}
	
	
	
	@Test
	@Order(value = 1)
	void testEmptyResponse(Vertx vertx, VertxTestContext testContext) {
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-all-valid-console-appender.yaml"), testContext.succeeding(id2 -> {
				HttpClient client = vertx.createHttpClient();
				
				client
					.request(HttpMethod.GET, 8080, "localhost", "/empty")
					.compose(req -> req.send().compose(HttpClientResponse::body))
					.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
							//assertThat(buffer.toString()).isEqualTo("Plop");
							System.out.println(buffer.toString());
							testContext.completeNow();
					})));
			}));
		}));
	}
}