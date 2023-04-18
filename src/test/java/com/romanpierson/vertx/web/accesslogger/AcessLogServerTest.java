package com.romanpierson.vertx.web.accesslogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import com.romanpierson.vertx.test.verticle.SimpleJsonResponseVerticle;
import com.romanpierson.vertx.web.accesslogger.verticle.AccessLoggerProducerVerticle;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * 
 * This class tests mostly generic issues with appenders
 * 
 */
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

	
}
