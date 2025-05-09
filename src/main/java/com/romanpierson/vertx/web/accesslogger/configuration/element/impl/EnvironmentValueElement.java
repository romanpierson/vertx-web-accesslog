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


import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromPostfixPatternIfApplicable;

import java.util.ArrayList;
import java.util.Collection;

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class EnvironmentValueElement implements AccessLogElement{

	private String value; 
	
	public static EnvironmentValueElement of(final String value) {
		
		EnvironmentValueElement element = new EnvironmentValueElement();
		element.value = System.getenv(value);
		
		return element;
	}
	
	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(1);
		
		extractBestPositionFromPostfixPatternIfApplicable(rawPattern, "env", 
			json -> EnvironmentValueElement.of(json.getString("configuration")))
			.ifPresent(foundPositions::add);
			
		return foundPositions;
	}
	
	@Override
	public Object getNativeValue(final JsonObject values) {
		
		return this.value;
		
	}
	

}
