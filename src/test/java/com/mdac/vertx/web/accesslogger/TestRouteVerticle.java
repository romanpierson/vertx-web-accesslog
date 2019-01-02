/*
 * Copyright (c) 2016-2019 Roman Pierson
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

import java.util.Arrays;

import com.mdac.vertx.web.accesslogger.appender.printstream.impl.PrintStreamAppenderOptions;
import com.mdac.vertx.web.accesslogger.impl.AccessLoggerOptions;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.Cookie;

/**
 * 
 * A simple test route to run and try out the access log
 * 
 * @author Roman Pierson
 *
 */
public class TestRouteVerticle extends AbstractVerticle {

	
	public static void main(String[] args) throws InterruptedException {
		
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		
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
			
				// Example how to specify a pattern and an explicit appender
				.handler(AccessLoggerHandler.create(new AccessLoggerOptions().setPattern("%t %m %D %T \"%{foo}C\" \"%{User-Agent}i\""), 
					                        Arrays.asList(
					                        		new PrintStreamAppenderOptions().setPrintStream(System.out)
					                        		)
					                        )
				);
		
		
		// Handle cookies
		router.route().handler(CookieHandler.create());
		
		router
			.route()
				.handler(routingContext -> {
					
					// Add a cookie to response for testing
					
					routingContext.addCookie(Cookie.cookie("foo", "bar"));
					
					routingContext.next();
					
				});
		
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

		server.requestHandler(router).listen(8080);
		
	}

}
