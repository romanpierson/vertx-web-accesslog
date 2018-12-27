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

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CookieElement implements AccessLogElement{

	private final String identifier; 
	
	public CookieElement() {
		this.identifier = null;
	}
	
	private CookieElement(final String identifier) {
		this.identifier = identifier;
	}

	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		final int index = rawPattern.indexOf("%{");
		
		if(index >= 0){
				
				int indexEndConfiguration = rawPattern.indexOf("}c");
			
				if(indexEndConfiguration > index)
				{
					String configurationString = rawPattern.substring(index + 2, indexEndConfiguration);
					
					return new ExtractedPosition(index, configurationString.length() + 4, new CookieElement(configurationString));
				}
			
		}
		
		
		return null;
	}

	@Override
	public String getFormattedValue(final JsonObject values) {
		
		if(!values.containsKey("cookies")){
			return null;
		}
		
		final JsonArray cookies = values.getJsonArray("cookies");
		
		for(int i = 0; i < cookies.size(); i++ ) {
			JsonObject cookie = cookies.getJsonObject(i);
			if(this.identifier.equals(cookie.getString("name"))){
				return cookie.getString("value");
			}
		}
		
		return null;
		
	}

}
