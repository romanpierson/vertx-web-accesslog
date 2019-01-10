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
package com.mdac.vertx.web.accesslogger.configuration.element.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.impl.Utils;

public class DateTimeElement implements AccessLogElement{

	private final DateFormat dateFormat;
	
	public DateTimeElement() {
		this.dateFormat = null;
	}
	
	private DateTimeElement(final DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		ExtractedPosition foundPosition = null;
		
		// Check if we have a configured datetime element
		int index = rawPattern.indexOf("%{");
		
		if(index >= 0){
				
				int indexEndConfigurationDatetime = rawPattern.indexOf("}t");
				int indexEndConfiguration = rawPattern.indexOf("}");
			
				if(indexEndConfigurationDatetime > index && (indexEndConfigurationDatetime == indexEndConfiguration)){
					String configurationString = rawPattern.substring(index + 2, indexEndConfigurationDatetime);
					
					foundPosition = new ExtractedPosition(index, configurationString.length() + 4, new DateTimeElement(deriveDateFormatFromConfigurationString(configurationString)));
				}
			
		}
		
		// Check if we have an unconfigured element
		final String requestPattern = "%t";
		index = rawPattern.indexOf(requestPattern);
			
		if(index >= 0){
			
			if(foundPosition == null || index < foundPosition.getStart()){
				foundPosition = new ExtractedPosition(index, requestPattern.length(), new DateTimeElement(Utils.createRFC1123DateTimeFormatter()));
			}
		}
		
		return foundPosition;
		
	}
	
	protected DateFormat deriveDateFormatFromConfigurationString(final String configurationString){
		
		if(configurationString != null && configurationString.length() > 0){
			
			final String[] configurationTokens = configurationString.split("\\|");
			
			if(configurationTokens != null && configurationTokens.length == 3){
			
				// Assume that is a configuration including format, timezone and locale
				
				DateFormat dtf = new SimpleDateFormat(configurationTokens[0], Locale.forLanguageTag(configurationTokens[2]));
				dtf.setTimeZone(TimeZone.getTimeZone(configurationTokens[1]));
				
				return dtf;
			} 
			else {
				
				// Assume this is just a format configuration
				DateFormat dtf = new SimpleDateFormat(configurationTokens[0], Locale.ENGLISH);
				dtf.setTimeZone(TimeZone.getTimeZone("GMT"));
				
				return dtf;
			}
		}
		
		return Utils.createRFC1123DateTimeFormatter();
		
	}

	@Override
	public String getFormattedValue(final JsonObject values) {
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append(this.dateFormat.format(values.getLong(Data.Type.START_TS_MILLIS.getFieldName())));
		
		return sb.toString();
	}

}
