/*
 * Copyright (c) 2016-2018 Roman Pierson
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
package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;

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
	
	/*@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
		if(!values.containsKey("cookies")){
			return "-";
		}
		
		final Set<Cookie> cookies = (Set<Cookie>) values.get("cookies");
		
		final Optional<Cookie> cookie = cookies.stream().filter(c -> this.identifier.equals(c.getName())).findFirst();
		
		return cookie.isPresent() ? cookie.get().getValue() : "-";
		
	}*/

	@Override
	public String getFormattedValue(JsonObject values) {
		// TODO Auto-generated method stub
		return null;
	}

}
