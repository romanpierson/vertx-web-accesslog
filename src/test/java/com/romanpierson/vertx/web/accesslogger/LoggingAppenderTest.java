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
import org.opentest4j.AssertionFailedError;

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
class LoggingAppenderTest {

	@Test
	@Order(value = 1)
	void testInvalidConfig(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			
			try {
				assertTrue(throwable instanceof AccessLoggerException);
				assertEquals("Failed to create appender with [com.romanpierson.vertx.web.accesslogger.appender.logging.impl.LoggingAppender]", throwable.getMessage());
				Throwable internalCause = throwable.getCause().getCause();
				assertTrue(internalCause instanceof IllegalArgumentException);
				assertEquals("loggerName must not be empty", internalCause.getMessage());
			}catch(AssertionFailedError ex) {
				testContext.failNow(ex);
			}
			testContext.completeNow();
		});
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
				
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-logging-appender-invalid-loggername.yaml", 8108));
				
		}));
	}
	
	@Test
	@Order(value = 2)
	void testInvalidLoggingAppenderWithMissingResolvedPatttern(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			
			try {
				assertTrue(throwable instanceof AccessLoggerException);
				assertEquals("Failed to create appender with [com.romanpierson.vertx.web.accesslogger.appender.logging.impl.LoggingAppender]", throwable.getMessage());
				Throwable internalCause = throwable.getCause().getCause();
				assertTrue(internalCause instanceof IllegalArgumentException);
				assertEquals("resolvedPattern must not be empty", internalCause.getMessage());
			}catch(AssertionFailedError ex) {
				testContext.failNow(ex);
			}
			testContext.completeNow();
		});
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
				
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-logging-appender-invalid-logpattern.yaml", 8109));
				
		}));
	}
	
	@Test
	@Order(value = 3)
	// This tests using slf4j/logback that at the end logs again to console so we can grab it
	void testWithValidData(Vertx vertx, VertxTestContext testContext) {
			
		PrintStream originalStream = System.out;
		ByteArrayOutputStream catchingStream = new ByteArrayOutputStream();
		
		vertx.exceptionHandler(throwable -> {
			testContext.failNow(throwable);
		});
		
		vertx.deployVerticle(new AccessLoggerProducerVerticle(),testContext.succeeding(id -> {
			vertx.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-logging-appender-valid.yaml", 8110), testContext.succeeding(id2 -> {
					
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.setOut(new PrintStream(catchingStream));
				
				HttpClient client = vertx.createHttpClient();
				
				client
					.request(HttpMethod.GET, 8110, "localhost", "/test")
					.compose(req -> req.send().compose(HttpClientResponse::body))
					.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
					
						// Ensure it got logged by slf4j/logback
						Thread.sleep(10);
						
						System.setOut(originalStream);
						
						// TODO this needs to be fixed
						//System.out.println(catchingStream);
						//assertEquals("/test", catchingStream.toString());
						
						testContext.completeNow();
						
					})));
			}));
		}));
	}
}