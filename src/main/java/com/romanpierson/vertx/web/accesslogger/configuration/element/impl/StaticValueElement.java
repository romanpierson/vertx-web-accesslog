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

import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromPostfixPatternIfApplicable;

import java.util.ArrayList;
import java.util.Collection;

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class StaticValueElement implements AccessLogElement{

	private String value; 
	
	public static StaticValueElement of(final String value) {
		
		StaticValueElement element = new StaticValueElement();
		element.value = value;
		
		return element;
	}
	
	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(1);
		
		extractBestPositionFromPostfixPatternIfApplicable(rawPattern, "static", 
			json -> StaticValueElement.of(json.getString("configuration")))
			.ifPresent(foundPositions::add);
			
		return foundPositions;
	}
	
	@Override
	public String getFormattedValue(final JsonObject values) {
		
		return this.value;
		
	}
	

}
