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
package com.romanpierson.vertx.web.accesslogger.configuration.element.impl;

import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromFixPatternIfApplicable;
import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromPostfixPatternAndAdditionalCheckIfApplicable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants;
import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class DateTimeElement implements AccessLogElement{

	private DateFormat dateFormat;
	
	public static DateTimeElement of(final String configurationString) {
		
		DateTimeElement element = new DateTimeElement();
		element.dateFormat = deriveDateFormatFromConfigurationString(configurationString);
		
		return element;
	}
	
	public static DateTimeElement of(final DateFormat dateFormat) {
		
		DateTimeElement element = new DateTimeElement();
		element.dateFormat = dateFormat;
		
		return element;
	}
	
	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(2);
		
		extractBestPositionFromPostfixPatternAndAdditionalCheckIfApplicable(rawPattern, "t",
			json -> json.getString("configuration", "").length() > 0, // At least we expect some configuration even if at the end we would fallback to default	
			json -> DateTimeElement.of(json.getString("configuration")))
			.ifPresent(foundPositions::add);
		
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%t", () -> DateTimeElement.of(createRFC1123DateTimeFormatter())).ifPresent(foundPositions::add);
		
		return foundPositions;
	}

	protected static DateFormat deriveDateFormatFromConfigurationString(final String configurationString){
		
		if(configurationString != null && configurationString.length() > 0){
			
			if("msec".equalsIgnoreCase(configurationString)) {
				return null;
			}
			
			final String[] configurationTokens = configurationString.split("\\|");
			
			if(configurationTokens != null && configurationTokens.length == 3){
			
				// Assume that is a configuration including format, timezone and locale
				
				DateFormat dtf = new SimpleDateFormat(configurationTokens[0], Locale.forLanguageTag(configurationTokens[2]));
				dtf.setTimeZone(TimeZone.getTimeZone(configurationTokens[1]));
				
				return dtf;
			} 
			else if(configurationTokens != null){
				
				// Assume this is just a format configuration
				DateFormat dtf = new SimpleDateFormat(configurationTokens[0], Locale.ENGLISH);
				dtf.setTimeZone(TimeZone.getTimeZone(AccessLoggerConstants.ZONE_UTC));
				
				return dtf;
			}
		}
		
		return createRFC1123DateTimeFormatter();
		
	}

	@Override
	public String getFormattedValue(final JsonObject values) {
		
		if(this.dateFormat == null) {
			return values.getLong(Data.Type.START_TS_MILLIS.getFieldName()).toString();
		} else {
			return this.dateFormat.format(values.getLong(Data.Type.START_TS_MILLIS.getFieldName()));
		}
	}
	
	private static DateFormat createRFC1123DateTimeFormatter() {
		
	    final DateFormat dtf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
	    dtf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    return dtf;
	    
	}

}
