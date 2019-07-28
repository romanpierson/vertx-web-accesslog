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

import java.util.concurrent.TimeUnit;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A simple test route to run and try out the access log
 * 
 * @author Roman Pierson
 *
 */
public class ServerSetupStarter {

	@SuppressWarnings("unused")
	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	public static void main(String[] args) throws InterruptedException {
		
		final Vertx vertx = Vertx.vertx(new VertxOptions().setWarningExceptionTimeUnit(TimeUnit.SECONDS).setWarningExceptionTime(4).setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS).setBlockedThreadCheckInterval(4));
		
		vertx.exceptionHandler(throwable -> {
			throwable.printStackTrace();
		});
		
		ConfigStoreOptions store = new ConfigStoreOptions().setType("file").setFormat("yaml")
										.setConfig(new JsonObject().put("path", "server-config.yaml"));

		ConfigRetriever retriever = ConfigRetriever.create(vertx,  new ConfigRetrieverOptions().addStore(store));
				
		retriever.getConfig(result -> {
			if(result.succeeded()) {
			
				result.result().getJsonArray("verticles").forEach(verticle -> {
					
					JsonObject verticleConfiguration = (JsonObject) verticle;
					
					String verticleClassName = verticleConfiguration.getString("verticleClassName");
					int instances = verticleConfiguration.getInteger("instances", 1);
					boolean isWorker = verticleConfiguration.getBoolean("isWorker", false);
					JsonObject config = verticleConfiguration.getJsonObject("config", new JsonObject());
					
					vertx.deployVerticle(verticleClassName, new DeploymentOptions().setInstances(instances).setWorker(isWorker).setConfig(config));
					
				});
				
			
			} else {
				result.cause().printStackTrace();
			}
		});
		
	}
	

}
