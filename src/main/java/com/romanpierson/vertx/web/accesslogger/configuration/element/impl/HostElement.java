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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;
import com.romanpierson.vertx.web.accesslogger.util.FormatUtility;

import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromFixPatternIfApplicable;

import io.vertx.core.json.JsonObject;

public class HostElement implements AccessLogElement{

	private Mode mode;
	
	private enum Mode{
		
		REMOTE_HOST,	
		LOCAL_HOST,
		LOCAL_PORT
		
	}
	
	public static HostElement of(final Mode mode){
		
		Objects.requireNonNull(mode, "mode must not be  null");
		
		HostElement element = new HostElement();
		
		element.mode = mode;
		
		return element;
		
	}
	
	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(3);
		
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%h", () -> HostElement.of(Mode.REMOTE_HOST)).ifPresent(foundPositions::add);
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%v", () -> HostElement.of(Mode.LOCAL_HOST)).ifPresent(foundPositions::add);
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%p", () -> HostElement.of(Mode.LOCAL_PORT)).ifPresent(foundPositions::add);
		
		return foundPositions;
	}
	
	@Override
	public String getFormattedValue(final JsonObject values) {
	
		switch (this.mode){
		
			case LOCAL_HOST:
				return FormatUtility.getStringOrNull(values, Data.Type.LOCAL_HOST.getFieldName());
			case LOCAL_PORT:
				return FormatUtility.getIntegerOrNull(values, Data.Type.LOCAL_PORT.getFieldName());
			case REMOTE_HOST:
				return FormatUtility.getStringOrNull(values, Data.Type.REMOTE_HOST.getFieldName());
			default:
				throw new IllegalStateException(String.format("mode %s not supported", this.mode.name()));
			
		}
	}
	
}
