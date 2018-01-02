/*
 * Copyright (c) 2016-2018 Roman Pierson
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

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class DurationElement implements AccessLogElement{

	public enum TimeUnit{
		SECONDS,
		MILLISECONDS
	}

	private final static Long INVALID_TS = new Long(-1);
	
	private final TimeUnit timeUnit;
	private final String startTSkey;
	private final String endTSkey;
	
	public DurationElement(){
	
		this.timeUnit = null;
		this.startTSkey = null;
		this.endTSkey = null;	
		
	}
	
	public DurationElement(final TimeUnit timeUnit){
		
		this.timeUnit = timeUnit;
		this.startTSkey = "startTSmillis";
		this.endTSkey = "endTSmillis";	
		
	}
	
	
	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		String patternMillis = "%D";
		
		int index = rawPattern.indexOf(patternMillis);
		
		if(index >= 0){
			
				return new ExtractedPosition(index, patternMillis.length(), new DurationElement(TimeUnit.MILLISECONDS));
			
		}
		
		String patternSeconds = "%T";
		
		index = rawPattern.indexOf(patternSeconds);
		
		if(index >= 0){
			
				return new ExtractedPosition(index, patternSeconds.length(), new DurationElement(TimeUnit.SECONDS));
			
		}
		
		return null;
	}

	
	@Override
	public String getFormattedValue(final JsonObject values) {
		
		final Long startTS = values.getLong(startTSkey, INVALID_TS);
		final Long endTS = values.getLong(endTSkey, INVALID_TS);
		
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
