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

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class DurationElement implements AccessLogElement{

	public enum TimeUnit{
		SECONDS,
		MILLISECONDS
	}

	private static final Long INVALID_TS = Long.valueOf(-1);
	
	private final TimeUnit timeUnit;
	
	public DurationElement(){
	
		this.timeUnit = null;
		
	}
	
	public DurationElement(final TimeUnit timeUnit){
		
		this.timeUnit = timeUnit;
		
	}
	
	
	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		ExtractedPosition foundPosition = null;
		
		String patternMillis = "%D";
		
		int index = rawPattern.indexOf(patternMillis);
		
		if(index >= 0){
			
			foundPosition = new ExtractedPosition(index, patternMillis.length(), new DurationElement(TimeUnit.MILLISECONDS));
			
		}
		
		String patternSeconds = "%T";
		
		index = rawPattern.indexOf(patternSeconds);
		
		if(index >= 0 && (foundPosition == null || index < foundPosition.getStart())){
			
			foundPosition = new ExtractedPosition(index, patternSeconds.length(), new DurationElement(TimeUnit.SECONDS));
			
		}
		
		return foundPosition;
	}

	
	@Override
	public String getFormattedValue(final JsonObject values) {
		
		final Long startTS = values.getLong(Data.Type.START_TS_MILLIS.getFieldName(), INVALID_TS);
		final Long endTS = values.getLong(Data.Type.END_TS_MILLIS.getFieldName(), INVALID_TS);
		
		if(startTS == null || endTS == null){
			return "-";
		}
		
		long duration = endTS.longValue() - startTS.longValue();
		
		if(TimeUnit.SECONDS.equals(this.timeUnit)){
			duration = duration / 1000;
		}
		
		return duration > 0 ? "" + duration : "0";
	}

}
