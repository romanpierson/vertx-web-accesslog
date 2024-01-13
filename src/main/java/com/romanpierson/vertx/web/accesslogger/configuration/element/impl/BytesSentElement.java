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
package com.romanpierson.vertx.web.accesslogger.configuration.element.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;
import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromFixPatternIfApplicable;

import io.vertx.core.json.JsonObject;

public class BytesSentElement implements AccessLogElement{

	private Mode mode;
	
	private static final Long DEFAULT_VALUE = Long.valueOf(0);
	
	private enum Mode{
		
		NO_BYTES_NULL,	
		NO_BYTES_DASH
		
	}
	
	public static BytesSentElement of(final Mode mode){
		
		BytesSentElement element = new BytesSentElement();
		element.mode = mode;
		
		return element;
	}
	
	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(2);
		
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%b", () -> BytesSentElement.of(Mode.NO_BYTES_DASH)).ifPresent(foundPositions::add);
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%B", () -> BytesSentElement.of(Mode.NO_BYTES_NULL)).ifPresent(foundPositions::add);

		return foundPositions;
		
	}
	
	@Override
	public Object getNativeValue(final JsonObject values) {
		
		final long bytes = values.getLong(Data.Type.BYTES_SENT.getFieldName(), DEFAULT_VALUE);
		
		if(bytes > 0) {
			return Long.valueOf(bytes);
		} else {
			return Mode.NO_BYTES_DASH.equals(this.mode) ? "-" : Long.valueOf(0);
		}
		
	}
	
}
