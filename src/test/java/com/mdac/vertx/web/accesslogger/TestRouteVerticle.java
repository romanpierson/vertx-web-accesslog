package com.mdac.vertx.web.accesslogger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

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
				.handler(AccessLoggerHandler.create("\"cs-uri\" cs-method %s %D %T" ));
		
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
