package com.romanpierson.vertx.web.accesslogger.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.romanpierson.vertx.test.verticle.SimpleJsonResponseVerticle;
import com.romanpierson.vertx.web.accesslogger.verticle.AccessLoggerProducerVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class ElementTest {

	/**
	 * 
	 * Tests all out of the box elements if they create the expected values in the log when we have an empty response
	 * 
	 * Using Event Bus Appender as its easiest to validate
	 * 
	 * @param vertx
	 * @param testContext
	 */
	@Test
	void testWithEmptyResponse(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			testContext.failNow(throwable);
		});
		
		vertx.eventBus().<JsonArray>consumer("target", message -> {
			
			assertEquals(2, message.body().size());
			assertEquals("-", message.body().getString(0));
			assertEquals("0", message.body().getString(1));
			
			testContext.completeNow();
		});
		
		vertx
			.deployVerticle(new AccessLoggerProducerVerticle())
				.onComplete(testContext.succeeding(deploymentId -> {
					vertx
						.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-elements-empty-result.yaml"))
						.onComplete(testContext.succeeding(deploymentId2 -> {
							
							WebClient client = WebClient.create(vertx);
							
							// Just to fix github actions issue
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// we dont care
							}
							
							client
								.request(HttpMethod.GET, 8080, "localhost", "/empty")
								.send()
								.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
									
								})));
							
						}));
				}));
		
		
	}
	
	/**
	 * 
	 * Tests all out of the box elements if they create the expected values in the log when we have a non empty response
	 * 
	 * Using Event Bus Appender as its easiest to validate
	 * 
	 * @param vertx
	 * @param testContext
	 */
	@Test
	void testWithNonEmptyResponse(Vertx vertx, VertxTestContext testContext) {
			
		vertx.exceptionHandler(throwable -> {
			testContext.failNow(throwable);
		});
		
		vertx.eventBus().<JsonArray>consumer("target", message -> {
			
			assertEquals(30, message.body().size());
			
			assertEquals("33", message.body().getString(0));
			assertEquals("33", message.body().getString(1));
			assertTrue(Long.parseLong(message.body().getString(2)) >= 0);
			assertTrue(Long.parseLong(message.body().getString(3)) >= 0);
			assertEquals("200", message.body().getString(4));
			assertEquals("200", message.body().getString(5));
			assertEquals("HTTP/1.1", message.body().getString(6));
			assertNotNull(message.body().getString(7));
			assertEquals("localhost", message.body().getString(8));
			assertEquals("8080", message.body().getString(9));
			assertEquals("GET", message.body().getString(10));
			assertEquals("GET", message.body().getString(11));
			assertEquals("GET /nonEmpty?foo=bar HTTP/1.1", message.body().getString(12));
			assertEquals("/nonEmpty?foo=bar", message.body().getString(13));
			assertEquals("/nonEmpty", message.body().getString(14));
			assertEquals("/nonEmpty", message.body().getString(15));
			assertEquals("?foo=bar", message.body().getString(16));
			assertEquals("?foo=bar", message.body().getString(17));
			// "Wed, 01 Feb 2023 20:19:14 GMT"
			// 858585858
			assertEquals("shouldShow", message.body().getString(20));
			assertEquals("xy", message.body().getString(21));
			assertEquals("envVal1", message.body().getString(22));
			assertEquals("header1val", message.body().getString(23));
			assertEquals("", message.body().getString(24));
			assertEquals("application/json; charset=utf-8", message.body().getString(25));
			assertEquals("", message.body().getString(26));
			assertEquals("cookie1Value", message.body().getString(27));
			assertEquals("cookie2Value", message.body().getString(28));
			assertEquals("", message.body().getString(29));
			
			testContext.completeNow();
		});
		
		vertx
			.deployVerticle(new AccessLoggerProducerVerticle())
				.onComplete(testContext.succeeding(deploymentId -> {
					vertx
						.deployVerticle(new SimpleJsonResponseVerticle("accesslog-config-elements-result.yaml"))
						.onComplete(testContext.succeeding(deploymentId2 -> {
							
							WebClient client = WebClient.create(vertx);
							
							// Just to fix github actions issue
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// we dont care
							}
							
							client
								.request(HttpMethod.GET, 8080, "localhost", "/nonEmpty?foo=bar")
								.putHeader("header1", "header1val")
								.putHeader("Cookie", "cookie1=cookie1Value; cookie2=cookie2Value")
								.send()
								.onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
										
								})));
							
						}));
				}));

	}
	
}
