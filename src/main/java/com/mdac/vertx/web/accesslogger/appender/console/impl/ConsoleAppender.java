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
package com.mdac.vertx.web.accesslogger.appender.console.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants;
import com.mdac.vertx.web.accesslogger.appender.Appender;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * An implementation of {@link Appender} that writes to System.out
 * 
 * @author Roman Pierson
 *
 */
public class ConsoleAppender implements Appender {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	private final String resolvedPattern;
	
	public ConsoleAppender(final JsonObject config){
		
		if(config == null || config.getString(AccessLoggerConstants.CONFIG_KEY_RESOLVED_PATTERN, "").trim().length() == 0){
			throw new IllegalArgumentException("resolvedPattern must not be empty");
		}
		
		this.resolvedPattern = config.getString(AccessLoggerConstants.CONFIG_KEY_RESOLVED_PATTERN);
		
		LOG.info("Created ConsoleAppender with resolvedLogPattern [{}]", this.resolvedPattern);
	}
	
	@Override
	public void push(JsonArray accessEvent) {
		
		Object [] parameterValues = getParameterValues(accessEvent);
			
		final String formattedString = String.format(this.resolvedPattern, parameterValues);
			
		System.out.println(formattedString);
			
	}
	
	private Object[] getParameterValues(final JsonArray values){
		
		final String[] parameterValues = new String[values.size()];

		int i = 0;
		for (final Object xValue : values.getList()) {
			parameterValues[i] = (String) xValue;
			i++;
		}
		
		return parameterValues;
		
	}

}
