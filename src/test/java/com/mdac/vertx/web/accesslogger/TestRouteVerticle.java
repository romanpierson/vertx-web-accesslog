package com.mdac.vertx.web.accesslogger;

import com.mdac.vertx.web.accesslogger.impl.AccessLoggerHandlerImpl;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
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

		//logTest();
		
		
		// Thread.sleep(6000);
	}
	
	private static void logTest(){
		
		Logger logger = LoggerFactory.getLogger(AccessLoggerHandlerImpl.class);
		
		for(int i=0;i<100000;i++){
			logger.info("Message" + i);
		}
		
	}
	
	@Override
	public void start() throws Exception {
		
		super.start();
		
		HttpServer server = this.vertx.createHttpServer();
		
		Router router = Router.router(vertx);

		router
			.route()
				.handler(AccessLoggerHandler.create(100));
		
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
