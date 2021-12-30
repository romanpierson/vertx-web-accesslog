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
package com.mdac.vertx.web.accesslogger.appender.eventbus.impl;

import com.mdac.vertx.web.accesslogger.appender.Appender;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * An implementation of {@link Appender} that forwards the access event to the given event bus target address
 * 
 * @author Roman Pierson
 *
 */
public class EventBusAppender implements Appender {

private static final String CONFIG_KEY_TARGET_ADDRESS = "targetAddress";

private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private final EventBus vertxEventBus;
	private final String eventBusTargetAddress;

	public EventBusAppender(final JsonObject config){
		
		if(config.getString(CONFIG_KEY_TARGET_ADDRESS, "").trim().length() == 0){
			throw new IllegalArgumentException(CONFIG_KEY_TARGET_ADDRESS + " must not be empty");
		}
		
		this.vertxEventBus = Vertx.currentContext().owner().eventBus();
		
		this.eventBusTargetAddress = config.getString(CONFIG_KEY_TARGET_ADDRESS);
		
		logger.info("Created EventBusAppender with eventBusTargetAddress [" + this.eventBusTargetAddress + "]");
		
	}
	
	@Override
	public void push(final JsonArray accessEvent) {
		
		vertxEventBus.send(this.eventBusTargetAddress,  accessEvent);
		
	}
	
}
