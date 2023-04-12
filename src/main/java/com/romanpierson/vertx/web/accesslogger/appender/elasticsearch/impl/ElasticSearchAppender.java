/*
 * Copyright (c) 2016-2023 Roman Pierson
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
	private static final String CONFIG_KEY_TIMESTAMP_POSITION = "timestampPosition";
	private static final String CONFIG_KEY_FIELD_NAMES = "fieldNames";

	private final EventBus vertxEventBus;
	
	private final int timestampPosition;
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
		
		this.timestampPosition = config.getInteger(CONFIG_KEY_TIMESTAMP_POSITION, 0);
		this.instanceIdentifier = config.getString(CONFIG_KEY_INSTANCE_IDENTIFER);
		this.fieldNames = config.getJsonArray(CONFIG_KEY_FIELD_NAMES).getList();
		
		logger.info("Created ElasticSearchAppender with " + CONFIG_KEY_INSTANCE_IDENTIFER + " [" + this.instanceIdentifier + "], " + CONFIG_KEY_TIMESTAMP_POSITION + " [" + this.timestampPosition + "], " + CONFIG_KEY_FIELD_NAMES + " " +this.fieldNames);
	}
	
	private String[] getParameterValues(final JsonArray values){
		
		final String[] parameterValues = new String[values.size()];

		int i = 0;
		for (final Object xValue : values.getList()) {
			parameterValues[i] = (String) xValue;
			i++;
		}
		
		return parameterValues;
		
	}

	@Override
	public void push(final JsonArray accessEvent) {
		
		final long metaTimestamp = this.timestampPosition < 0 ? System.currentTimeMillis() : Long.parseLong(accessEvent.getString(this.timestampPosition));
			
			// Just send the accesslog event to the indexer
			JsonObject jsonMeta = new JsonObject();
			jsonMeta.put(Field.TIMESTAMP, metaTimestamp);
			jsonMeta.put(Field.INSTANCE_IDENTIFIER, this.instanceIdentifier);
			
			JsonObject jsonMessage = new JsonObject();
			
			String [] parameterValues = getParameterValues(accessEvent);
			
			int i = 0;
			for(String fieldName : fieldNames) {
				if(this.timestampPosition != i) {
					jsonMessage.put(fieldName, parameterValues[i]);
				}
				i++;
			}
			
			JsonObject json = new JsonObject()
					.put(Field.META, jsonMeta)
					.put(Field.MESSAGE, jsonMessage);
			
			this.vertxEventBus.send(ElasticSearchAppenderConfig.ELASTICSEARCH_INDEXER_EVENTBUS_EVENT_NAME,  json);
			
	}
	
}
