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
package com.mdac.vertx.web.accesslogger.appender.printstream.impl;

import java.io.PrintStream;
import java.util.Collection;

import com.mdac.vertx.web.accesslogger.appender.Appender;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;

import io.vertx.core.json.JsonObject;

public class PrintStreamAppender implements Appender {

	private final PrintStream printStream;
	private final Collection<AccessLogElement> accessLogElements;
	private String resolvedPattern;
	
	public PrintStreamAppender(final PrintStreamAppenderOptions appenderOptions, final Collection<AccessLogElement> accessLogElements){
		
		if(appenderOptions == null){
			throw new IllegalArgumentException("appenderOptions must not be null");
		}
		
		if(appenderOptions.getPrintStream() == null){
			throw new IllegalArgumentException("appenderOptions.printStream must not be null");
		}
		
		if(accessLogElements == null || accessLogElements.size() < 1){
			throw new IllegalArgumentException("accessLogElements must contain at least one element");
		}
		
		this.printStream = appenderOptions.getPrintStream();
		this.accessLogElements = accessLogElements;
		
	}
	
	@Override
	public void push(Collection<JsonObject> accessEvents) {
		
		for(JsonObject value : accessEvents){
			
			Object [] parameterValues = getParameterValues(value);
			
			final String formattedString = String.format(this.resolvedPattern, parameterValues);
			
			this.printStream.println(formattedString);
			
		}
		
	}
	
	private Object[] getParameterValues(final JsonObject values){
		
		final String[] parameterValues = new String[accessLogElements.size()];

		int i = 0;
		for(final AccessLogElement alElement : accessLogElements){
			final String formattedValue = alElement.getFormattedValue(values);
			parameterValues[i] = formattedValue != null ? formattedValue : "";
			i++;
		}
		
		return parameterValues;
		
	}

	@Override
	public boolean requiresResolvedPattern() {
		
		return true;
		
	}

	@Override
	public void setResolvedPattern(final String resolvedPattern) {
		
		if(resolvedPattern == null || resolvedPattern.trim().length() < 1){
			throw new IllegalArgumentException("resolvedPattern must not be empty");
		}
		
		if(this.resolvedPattern != null){
			throw new IllegalStateException("resolvedPattern is already set");
		}
		
		this.resolvedPattern = resolvedPattern;
		
	}
	
}
