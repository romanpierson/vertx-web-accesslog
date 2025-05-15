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
import java.util.Collections;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;
import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromPostfixPatternIfApplicable;

import io.vertx.core.json.JsonObject;

public class HeaderElement implements AccessLogElement{

	private Mode mode;
	private String identifier; 
	
	private enum Mode{
		
		INCOMING,	
		OUTGOING
		
	}
	
	public static HeaderElement of(final Mode mode, final String identifier) {
		
		HeaderElement element = new HeaderElement();
		element.mode = mode;
		element.identifier = identifier.toLowerCase();
		
		return element;
	}

	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(2);
		
		extractBestPositionFromPostfixPatternIfApplicable(rawPattern, "i", 
			json -> HeaderElement.of(Mode.INCOMING, json.getString("configuration")))
			.ifPresent(foundPositions::add);
		
		extractBestPositionFromPostfixPatternIfApplicable(rawPattern, "o", 
			json -> HeaderElement.of(Mode.OUTGOING, json.getString("configuration")))
			.ifPresent(foundPositions::add);
			
		return foundPositions;
	}
	
	@Override
	public Object getNativeValue(final JsonObject values) {
		
		final JsonObject headers =  Mode.INCOMING.equals(this.mode) ? values.getJsonObject(Data.Type.REQUEST_HEADERS.getFieldName()) : values.getJsonObject(Data.Type.RESPONSE_HEADERS.getFieldName());
		
		return headers.getString(this.identifier, null);
		
	}
	
	@Override
	public Collection<Data.Type> claimDataParts() {
		
		return Mode.INCOMING.equals(this.mode) ? Collections.singleton(Data.Type.REQUEST_HEADERS) : Collections.singleton(Data.Type.RESPONSE_HEADERS);
		
	}

}
