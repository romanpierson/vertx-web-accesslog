package com.romanpierson.vertx.test.verticle;


import com.romanpierson.vertx.web.accesslogger.AccessLoggerHandler;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class SimpleJsonResponseVerticle extends AbstractVerticle {

	private final String configFile;
	
	public SimpleJsonResponseVerticle(String configFile) {
		this.configFile = configFile;
	}
	
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        
    	final Router router = Router.router(vertx);
    	
    	ConfigStoreOptions store = new ConfigStoreOptions().setType("file").setFormat("yaml")
				.setConfig(new JsonObject().put("path", configFile));

    	ConfigRetriever retriever = ConfigRetriever.create(vertx,  new ConfigRetrieverOptions().addStore(store));

    	retriever.getConfig(result -> {
    		
    		if(result.succeeded()) {
    			router
    	    		.route()
    	    		.handler(BodyHandler.create())
    	    		.handler(AccessLoggerHandler.create(result.result())
    	    	);
    			
    			router
    				.post("/posttest")
    					.handler(ctx -> {
    						
    						//System.out.println(ctx.body());

    						final JsonObject resultJson = new JsonObject();
        					
    						resultJson.put("uri", ctx.request().uri());
    					
    						ctx.response().putHeader("Content-Type", "application/json; charset=utf-8").end(resultJson.encodePrettily());
    					});
    			
    			router
					.get("/empty")
						.handler(ctx -> {
					
							ctx.response().putHeader("Content-Type", "application/json; charset=utf-8").end();
						});
    			
    			router
    				.get()
    					.handler(ctx -> {
    					
    						final JsonObject resultJson = new JsonObject();
    					
    						resultJson.put("uri", ctx.request().uri());
    					
    						ctx.response().putHeader("Content-Type", "application/json; charset=utf-8").end(resultJson.encodePrettily());
    					});
    			
    			vertx.createHttpServer().requestHandler(router).listen(8080);
    			
    			startPromise.complete();
    		}
    		
    	});
    	
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
    	
    	stopPromise.complete();
    	
    }
}
