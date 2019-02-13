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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;

/**
 * 
 * A simple test route to run and try out the access log
 * 
 * @author Roman Pierson
 *
 */
public class HttpServerVerticle extends AbstractVerticle {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void start() throws Exception {
		
		super.start();
		
		HttpServer server = this.vertx.createHttpServer();
		
		Router router = Router.router(vertx);
		
		final JsonObject accessLogHandlerConfig = this.config().getJsonObject("accesslogHandler", null);
		
		//System.out.println(accessLogHandlerConfig.encodePrettily());
		
		if(accessLogHandlerConfig != null) {
			router.route().handler(AccessLoggerHandler.create(accessLogHandlerConfig));
		}
		
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
			
					  LOG.info("Got request for [{}]", routingContext.request().uri());
					  
					  // Write to the response and end it
					  response.end("Hello World from Vert.x-Web!");
		});

		long startTS = System.currentTimeMillis();
		
		int port = this.config().getInteger("port");
		
		server.requestHandler(router).listen(port, ar -> {
			if(ar.succeeded()) {
				LOG.info("Successfully started http server on port [{}] in [{}] ms", port, System.currentTimeMillis() - startTS);
			} else {
				LOG.error("Failed to start http server", ar.cause());
			}
		});
		
	}

}
