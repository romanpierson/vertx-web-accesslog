package com.romanpierson.vertx.web.accesslogger;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import com.romanpierson.vertx.test.verticle.SimpleJsonResponseVerticle;
import com.romanpierson.vertx.web.accesslogger.exception.AccessLoggerException;
import com.romanpierson.vertx.web.accesslogger.verticle.AccessLoggerProducerVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class ConsoleAppenderTest {

	@Test
	@Order(value = 1)
	void testInvalidConsoleAppenderWithMissingResolvedPatttern(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			throwable.printStackTrace();
			assertTrue(throwable instanceof AccessLoggerException);
			assertEquals("Failed to create appender with [com.romanpierson.vertx.web.accesslogger.appender.console.impl.ConsoleAppender]", throwable.getMessage());
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
	@Order(value = 2)
	void testWithValidData(Vertx vertx, VertxTestContext testContext) {
			
		PrintStream originalStream = System.out;
		ByteArrayOutputStream catchingStream = new ByteArrayOutputStream();
		
		vertx.exceptionHandler(throwable -> {
			testContext.failNow(throwable);
		});
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-console-appender-valid.yaml"), testContext.succeeding(id2 -> {
					
				System.setOut(new PrintStream(catchingStream));
				
				HttpClient client = vertx.createHttpClient();
				
				// Just to fix github actions issue
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// we dont care
				}
				
				client
					.request(HttpMethod.GET, 8080, "localhost", "/test")
					.compose(req -> req.send().compose(HttpClientResponse::body))
					.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
					
						assertEquals("/test\n", catchingStream.toString()); // Has a newline at the end....
						
						System.setOut(originalStream);
						
						testContext.completeNow();
						
					})));
			}));
		}));
	}
}
