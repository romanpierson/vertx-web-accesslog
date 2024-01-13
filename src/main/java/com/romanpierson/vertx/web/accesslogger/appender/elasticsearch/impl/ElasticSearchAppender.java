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
package com.romanpierson.vertx.web.accesslogger.appender.elasticsearch.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants;
import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.ElasticSearchAppenderConfig;
import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.ElasticSearchAppenderConfig.Field;
import com.romanpierson.vertx.web.accesslogger.appender.Appender;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * An implementation of {@link Appender} that writes to {ElasticSearchIndexerVerticle}
 * 
 * see https://github.com/romanpierson/vertx-elasticsearch-indexer
 * 
 * @author Roman Pierson
 *
 */
public class ElasticSearchAppender implements Appender {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private static final String CONFIG_KEY_INSTANCE_IDENTIFER = "instanceIdentifier";
	private static final String CONFIG_KEY_FIELD_NAMES = "fieldNames";

	private final EventBus vertxEventBus;
	
	private final String instanceIdentifier;
	private final Collection<String> fieldNames;
	
	@SuppressWarnings("unchecked")
	public ElasticSearchAppender(final JsonObject config){
		
		if (config.getString(CONFIG_KEY_INSTANCE_IDENTIFER, "").trim().length() == 0) {
			throw new IllegalArgumentException(CONFIG_KEY_INSTANCE_IDENTIFER + " must not be empty");
		} else if (config.getJsonArray(CONFIG_KEY_FIELD_NAMES, new JsonArray()).isEmpty()) {
			throw new IllegalArgumentException(CONFIG_KEY_FIELD_NAMES + " must not be empty");
		}
		
		this.vertxEventBus = Vertx.currentContext().owner().eventBus();
		
		this.instanceIdentifier = config.getString(CONFIG_KEY_INSTANCE_IDENTIFER);
		this.fieldNames = config.getJsonArray(CONFIG_KEY_FIELD_NAMES).getList();
		
		logger.info("Created ElasticSearchAppender with " + CONFIG_KEY_INSTANCE_IDENTIFER + " [" + this.instanceIdentifier + "], " + CONFIG_KEY_FIELD_NAMES + " " +this.fieldNames);
	}
	
	private Object[] getParameterValuesCopy(List<Object> rawAccessElementValues){
		
		final Object[] parameterValues = new Object[rawAccessElementValues.size()];

		int i = 0;
		for (final Object xValue : rawAccessElementValues) {
			parameterValues[i] = xValue;
			i++;
		}
		
		return parameterValues;
		
	}
	
	@Override
	public void push(final List<Object> rawAccessElementValues, Map<String, Object> internalValues) {
		
		// Just send the accesslog event to the indexer
		JsonObject jsonMeta = new JsonObject();
		jsonMeta.put(Field.TIMESTAMP, (Long) internalValues.get(AccessLoggerConstants.InternalValues.TIMESTAMP));
		jsonMeta.put(Field.INSTANCE_IDENTIFIER, this.instanceIdentifier);
			
		JsonObject jsonMessage = new JsonObject();
			
		// TODO do we really need a copy?
		Object [] nativeParameterValues = getParameterValuesCopy(rawAccessElementValues);
			
		int i = 0;
		for(String fieldName : fieldNames) {
				
			if(nativeParameterValues[i] != null){
				// Values having no value we dont send either
				jsonMessage.put(fieldName, nativeParameterValues[i]);
			}
				
			i++;
		}
			
		JsonObject json = new JsonObject()
					.put(Field.META, jsonMeta)
					.put(Field.MESSAGE, jsonMessage);
			
		this.vertxEventBus.send(ElasticSearchAppenderConfig.ELASTICSEARCH_INDEXER_EVENTBUS_EVENT_NAME,  json);
			
	}
	
}
