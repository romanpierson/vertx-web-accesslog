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

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;
import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromFixPatternIfApplicable;
import com.romanpierson.vertx.web.accesslogger.util.VersionUtility;

import io.vertx.core.json.JsonObject;

public class VersionElement implements AccessLogElement{

	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(1);
		
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%H", VersionElement::new).ifPresent(foundPositions::add);
		
		return foundPositions;
		
	}

	@Override
	public String getFormattedValue(final JsonObject values) {
		
	    return VersionUtility.getFormattedValue(values);
		
	}
	
}
