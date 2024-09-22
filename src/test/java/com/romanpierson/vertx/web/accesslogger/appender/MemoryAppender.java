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
package com.romanpierson.vertx.web.accesslogger.appender;

import java.util.List;
import java.util.Map;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants;

import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import static com.romanpierson.vertx.web.accesslogger.util.FormatUtility.getStringifiedParameterValues;

/**
 * 
 * An implementation of {@link Appender} that writes to System.out
 * 
 * @author Roman Pierson
 *
 */
public class MemoryAppender implements Appender {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private final String resolvedPattern;
	
	public MemoryAppender(final JsonObject config){
		
		if(config == null || config.getString(AccessLoggerConstants.CONFIG_KEY_RESOLVED_PATTERN, "").trim().length() == 0){
			throw new IllegalArgumentException("resolvedPattern must not be empty");
		}
		
		this.resolvedPattern = config.getString(AccessLoggerConstants.CONFIG_KEY_RESOLVED_PATTERN);
		
		logger.info("Created ConsoleAppender with resolvedLogPattern [" + this.resolvedPattern + "]");
		
	}
	
	@Override
	public void push(List<Object> rawAccessElementValues, Map<String, Object> internalValues) {
			
		final String formattedString = String.format(this.resolvedPattern, getStringifiedParameterValues(rawAccessElementValues));
			
		System.out.println(formattedString);
			
	}
	

}
