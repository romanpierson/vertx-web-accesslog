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
import java.util.Collections;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CookieElement implements AccessLogElement{

	private String identifier; 
	
	public static CookieElement of(final String identifier) {
		
		CookieElement element = new CookieElement();
		element.identifier = identifier;
		
		return element;
	}

	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(1);
		
		extractBestPositionFromPostfixPatternIfApplicable(rawPattern, "C", 
			json -> CookieElement.of(json.getString("configuration")))
			.ifPresent(foundPositions::add);
		
		return foundPositions;
	}

	@Override
	public String getFormattedValue(final JsonObject values) {
		
		if(!values.containsKey(Data.Type.COOKIES.getFieldName())){
			return null;
		}
		
		final JsonArray cookies = values.getJsonArray(Data.Type.COOKIES.getFieldName());
		
		for(int i = 0; i < cookies.size(); i++ ) {
			JsonObject cookie = cookies.getJsonObject(i);
			if(this.identifier.equals(cookie.getString(Data.Fields.COOKIE_NAME))){
				return cookie.getString(Data.Fields.COOKIE_VALUE);
			}
		}
		
		return null;
		
	}

	@Override
	public Collection<Data.Type> claimDataParts() {
		
		return Collections.singleton(Data.Type.COOKIES);
		
	}

}
