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
import static com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver.extractBestPositionFromFixPatternIfApplicable;
import com.romanpierson.vertx.web.accesslogger.util.VersionUtility;

import io.vertx.core.json.JsonObject;

public class RequestElement implements AccessLogElement{

	private RequestLogMode requestLogMode;
	
	private enum RequestLogMode{
		
		APACHE_FIRST_REQUEST_LINE,	// Identical to %m %U%q %H" will log the method, path, query-string, and protocol
		URI,
		QUERY_STRING,
		URI_QUERY
		
	}
	
	public static RequestElement of(final RequestLogMode requestLogMode) {
		
		RequestElement element = new RequestElement();
		element.requestLogMode = requestLogMode;
		
		return element;
	}

	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(final String rawPattern) {
		
		Collection<ExtractedPosition> foundPositions = new ArrayList<>(6);
		
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "%r", () -> RequestElement.of(RequestLogMode.APACHE_FIRST_REQUEST_LINE)).ifPresent(foundPositions::add);
		extractBestPositionFromFixPatternsIfApplicable(rawPattern, Arrays.asList("%U", "cs-uri-stem"), () -> RequestElement.of(RequestLogMode.URI)).ifPresent(foundPositions::addAll);
		extractBestPositionFromFixPatternsIfApplicable(rawPattern, Arrays.asList("%q", "cs-uri-query"), () -> RequestElement.of(RequestLogMode.QUERY_STRING)).ifPresent(foundPositions::addAll);
		extractBestPositionFromFixPatternIfApplicable(rawPattern, "cs-uri", () -> RequestElement.of(RequestLogMode.URI_QUERY)).ifPresent(foundPositions::add);
		
		return foundPositions;
	}


	@Override
	public String getFormattedValue(JsonObject values) {
		
		final StringBuilder sb = new StringBuilder();
		
		if(RequestLogMode.APACHE_FIRST_REQUEST_LINE.equals(this.requestLogMode)){
			
			sb.append(values.getString(Data.Type.METHOD.getFieldName())).append(' ');
			
		}
		
		if(!RequestLogMode.QUERY_STRING.equals(this.requestLogMode)){
			sb.append(values.getString(Data.Type.URI.getFieldName()));
		}
		
		if(!RequestLogMode.URI.equals(this.requestLogMode)
			&& values.getString(Data.Type.QUERY.getFieldName(), null) != null){
			sb.append('?').append(values.getString(Data.Type.QUERY.getFieldName(), null));
		}
		
		
		if(RequestLogMode.APACHE_FIRST_REQUEST_LINE.equals(this.requestLogMode)){
			
			sb.append(' ').append(VersionUtility.getFormattedValue(values));
			
		}
		
		return sb.toString();
	}
	
	

}
