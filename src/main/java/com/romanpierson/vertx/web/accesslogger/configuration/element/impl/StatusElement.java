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
import java.util.Arrays;
import java.util.Collection;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;
import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromFixPatternsIfApplicable;

import io.vertx.core.json.JsonObject;

public class StatusElement implements AccessLogElement{

	
	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(2);
		
		extractBestPositionFromFixPatternsIfApplicable(rawPattern, Arrays.asList("sc-status", "%s"), StatusElement::new).ifPresent(foundPositions::addAll);
		
		return foundPositions;
		
	}
	
	@Override
	public String getFormattedValue(final JsonObject values) {
		
		return Integer.toString(values.getInteger(Data.Type.STATUS.getFieldName()));
		
	}
	
}
