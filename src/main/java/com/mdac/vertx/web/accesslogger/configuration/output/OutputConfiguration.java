/*
 * Copyright (c) 2016-2017 Roman Pierson
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
package com.mdac.vertx.web.accesslogger.configuration.output;

import java.util.Collection;
import java.util.Map;

import com.mdac.vertx.web.accesslogger.configuration.pattern.AccessLogElement;

import io.vertx.core.logging.Logger;

public class OutputConfiguration {

	private String formatterPattern;
	
	private Collection<AccessLogElement> accessLogElements;
	
	private Collection<Logger> loggers;
	
	public OutputConfiguration(String formatterPattern, Collection<AccessLogElement> accessLogElements,
			Collection<Logger> loggers) {
		super();
		this.formatterPattern = formatterPattern;
		this.accessLogElements = accessLogElements;
		this.loggers = loggers;
	}

	public void doLog(final Map<String, Object> values){
	
		String [] parameterValues = getParameterValues(values);
		
		final String formattedString = String.format(formatterPattern, parameterValues);
		
		for(final Logger logger : loggers){
			logger.info(formattedString);
		}
		
	}
	
	private String[] getParameterValues(final Map<String, Object> values){
		
		final String[] parameterValues = new String[accessLogElements.size()];

		int i = 0;
		for(final AccessLogElement alElement : accessLogElements){
			parameterValues[i] = alElement.getFormattedValue(values);
			i++;
		}
		
		return parameterValues;
		
	}
	
}
