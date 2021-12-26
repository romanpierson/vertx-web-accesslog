/*
 * Copyright (c) 2016-2019 Roman Pierson
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
package com.mdac.vertx.web.accesslogger.configuration.element.impl;

import java.util.Arrays;
import java.util.Collection;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;
import com.mdac.vertx.web.accesslogger.util.VersionUtility;

import io.vertx.core.json.JsonObject;

public class RequestElement implements AccessLogElement{

	private final RequestLogMode requestLogMode;
	
	private enum RequestLogMode{
		
		APACHE_FIRST_REQUEST_LINE,	// Identical to %m %U%q %H" will log the method, path, query-string, and protocol
		URI,
		QUERY_STRING,
		URI_QUERY
		
	}
	
	public RequestElement() {
		this.requestLogMode = null;
	}
	
	private RequestElement(final RequestLogMode requestLogMode) {
		this.requestLogMode = requestLogMode;
	}

	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		ExtractedPosition foundPosition = null;
		
		// Apache request format
		final String requestPattern = "%r";
		int index = rawPattern.indexOf(requestPattern);
			
		if(index >= 0){
				
			foundPosition = new ExtractedPosition(index, requestPattern.length(), new RequestElement(RequestLogMode.APACHE_FIRST_REQUEST_LINE));
			
		}
		
		// URI mode only
		final Collection<String> urlPatterns = Arrays.asList("%U", "cs-uri-stem");
		
		for (final String urlPattern : urlPatterns){
			index = rawPattern.indexOf(urlPattern);
			
			if(index >= 0 && (foundPosition == null || index < foundPosition.getStart())){
				
				foundPosition = new ExtractedPosition(index, urlPattern.length(), new RequestElement(RequestLogMode.URI));
				
			}
		}
		
		// URI query mode only
		final Collection<String> queryOnlyPatterns = Arrays.asList("%q", "cs-uri-query");
				
		for (final String queryOnlyPattern : queryOnlyPatterns){
			index = rawPattern.indexOf(queryOnlyPattern);
					
			if(index >= 0 && (foundPosition == null || index < foundPosition.getStart())){
				
				foundPosition = new ExtractedPosition(index, queryOnlyPattern.length(), new RequestElement(RequestLogMode.QUERY_STRING));
				
			}
		}
		
		// Complete URI including query
		final String uriQueryPattern = "cs-uri";
		index = rawPattern.indexOf(uriQueryPattern);
			
		if(index >= 0 && (foundPosition == null || index < foundPosition.getStart())){
				
			foundPosition = new ExtractedPosition(index, uriQueryPattern.length(), new RequestElement(RequestLogMode.URI_QUERY));
			
		}
		
		return foundPosition;
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
