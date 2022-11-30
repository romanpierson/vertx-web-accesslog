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
package com.mdac.vertx.web.accesslogger.configuration.element.impl;

import java.util.Collection;
import java.util.Collections;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class HeaderElement implements AccessLogElement{

	private final Mode mode;
	private final String identifier; 
	
	private enum Mode{
		
		INCOMING,	
		OUTGOING
		
	}
	public HeaderElement() {
		this.mode = null;
		this.identifier = null;
	}
	
	private HeaderElement(final Mode mode, final String identifier) {
		this.mode = mode;
		this.identifier = identifier;
	}

	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		final int index = rawPattern.indexOf("%{");
		
		if(index >= 0){
				
			
				int indexEndConfiguration = rawPattern.indexOf('}');
			
				if(indexEndConfiguration > index 
					&& rawPattern.length() > indexEndConfiguration
					&& (rawPattern.charAt(indexEndConfiguration + 1) == 'i' || rawPattern.charAt(indexEndConfiguration + 1) == 'o'))
				{
					String configurationString = rawPattern.substring(index + 2, indexEndConfiguration);
					
					return new ExtractedPosition(index, configurationString.length() + 4, new HeaderElement(rawPattern.charAt(indexEndConfiguration + 1) == 'i' ? Mode.INCOMING : Mode.OUTGOING, configurationString));
				}
			
		}
		
		
		return null;
	}
	
	@Override
	public String getFormattedValue(final JsonObject values) {
		
		final JsonObject headers =  Mode.INCOMING.equals(this.mode) ? values.getJsonObject(Data.Type.REQUEST_HEADERS.getFieldName()) : values.getJsonObject(Data.Type.RESPONSE_HEADERS.getFieldName());
		
		return headers.getString(this.identifier, null);
		
	}
	
	@Override
	public Collection<Data.Type> claimDataParts() {
		
		return Mode.INCOMING.equals(this.mode) ? Collections.singleton(Data.Type.REQUEST_HEADERS) : Collections.singleton(Data.Type.RESPONSE_HEADERS);
		
	}

}
