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
package com.romanpierson.vertx.web.accesslogger.util;

import io.vertx.core.json.JsonObject;

import java.util.List;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class FormatUtility {

	private final static Logger logger = LoggerFactory.getLogger(FormatUtility.class.getName());

	private FormatUtility() {}
	
	public static String getStringOrNull(final JsonObject values, final String fieldName) {
		
		return values.getString(fieldName) != null ?  values.getString(fieldName) : null;
		
	}
	
	public static String getIntegerOrNull(final JsonObject values, final String fieldName) {
		
		return values.getInteger(fieldName) != null ?  Integer.toString(values.getInteger(fieldName)) : null;
		
	}
	
	public static Object[] getStringifiedParameterValues(final List<Object> rawValues){
		
		final String[] parameterValues = new String[rawValues.size()];

		int i = 0;
		for (final Object xValue : rawValues) {
			
			if(xValue == null){
				parameterValues[i] = "";
			} 
			else if (xValue instanceof Long) {
				parameterValues[i] = Long.toString((Long) xValue);
			} else if (xValue instanceof Integer) {
				parameterValues[i] = Integer.toString((Integer) xValue);
			}else if (xValue instanceof String) {
				parameterValues[i] = (String) xValue;
			} else {
				logger.error("Unrecognized type" + xValue.getClass());
			}
			
			i++;
		}
		
		return parameterValues;
		
	}

}
