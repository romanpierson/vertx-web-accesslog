/*
 * Copyright (c) 2016-2017 Roman Pierson
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 
 * which accompanies this distribution.
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package com.mdac.vertx.web.accesslogger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;

/**
 * 
 * A simple test route to run and try out the access log
 * 
 * @author Roman Pierson
 *
 */
public class TestRouteVerticle extends AbstractVerticle {

	
	public static void main(String[] args) throws InterruptedException {
		
		// Delegating to SLF4J in order to use logback as backend (see example logback.xml)
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		
		// Log4J Native
		// System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4jLogDelegateFactory");
		
		// Log4J2 Native
		//System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
		
		final Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new TestRouteVerticle());

	}
	
	
	@Override
	public void start() throws Exception {
		
		super.start();
		
		HttpServer server = this.vertx.createHttpServer();
		
		Router router = Router.router(vertx);

		router
			.route()
				.handler(AccessLoggerHandler.create("%r \"%{referrer}i\" \"%{user-Agent}i\" \"%{Content-Type}o\" %D %T %B"));
		
		// Handle cookies
		router.route().handler(CookieHandler.create());
		
		router
			.route("/nocontent")
				.handler(routingContext -> {
					
					// Example handler that generates no content
					
					HttpServerResponse response = routingContext.response();
					response.end();
					
				});
		
		router
			.route()
				.handler(routingContext -> {
					
					  // This handler will be called for every request
					  HttpServerResponse response = routingContext.response();
					  response.putHeader("content-type", "text/plain");
			
					  // Write to the response and end it
					  response.end("Hello World from Vert.x-Web!");
		});

		server.requestHandler(router::accept).listen(8080);
		
	}

}
