/*
 * Copyright (c) 2016-2025 Roman Pierson
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

import java.util.ArrayList;
import java.util.Collection;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;
import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromFixPatternIfApplicable;

import io.vertx.core.json.JsonObject;

public class DurationElement implements AccessLogElement{

	private enum TimeUnit{
		
		SECONDS,
		MILLISECONDS
		
	}
	
	private TimeUnit timeUnit;
	
	public static DurationElement of(final TimeUnit timeUnit){
		
		DurationElement element = new DurationElement();
		element.timeUnit = timeUnit;
		
		return element;
	}
	
	
	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(2);
		
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%D", () -> DurationElement.of(TimeUnit.MILLISECONDS)).ifPresent(foundPositions::add);
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%T", () -> DurationElement.of(TimeUnit.SECONDS)).ifPresent(foundPositions::add);
		
		return foundPositions;
	}
	
	@Override
	public Object getNativeValue(final JsonObject values) {
		
		final Long startTS = values.getLong(Data.Type.START_TS_MILLIS.getFieldName());
		final Long endTS = values.getLong(Data.Type.END_TS_MILLIS.getFieldName());
		
		long duration = endTS.longValue() - startTS.longValue();
		
		if(TimeUnit.SECONDS.equals(this.timeUnit)){
			duration = duration / 1000;
		}
		
		return duration > 0 ? duration : Long.valueOf(0);
	}
	

}
