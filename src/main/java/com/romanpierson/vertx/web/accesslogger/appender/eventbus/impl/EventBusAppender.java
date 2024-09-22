/*
 * Copyright (c) 2016-2024 Roman Pierson
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
package com.romanpierson.vertx.web.accesslogger.appender.eventbus.impl;

import java.util.List;
import java.util.Map;

import com.romanpierson.vertx.web.accesslogger.appender.Appender;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
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
	public void push(final List<Object> rawAccessElementValues, Map<String, Object> internalValues) {
		
		// TODO should we make a copy?
		
		vertxEventBus.send(this.eventBusTargetAddress,  convertToJsonArray(rawAccessElementValues));
		
	}
	
	private JsonArray convertToJsonArray(List<Object> rawAccessElementValues){
		
		JsonArray s = new JsonArray();
		
		rawAccessElementValues.forEach(x -> s.add(x));
		
		return s;
	}
	
}
