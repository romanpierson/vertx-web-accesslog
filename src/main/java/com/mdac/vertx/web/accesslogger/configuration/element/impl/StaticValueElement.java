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

import io.vertx.core.json.JsonObject;

public class StaticValueElement implements AccessLogElement{

	private final String value; 
	
	private StaticValueElement(final String value) {
		this.value = value;
	}
	
	public StaticValueElement() {
		this.value = null;
	}

	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		final int index = rawPattern.indexOf("%{");
		
		if(index >= 0){
				
			
				int indexEndConfiguration = rawPattern.indexOf("}");
			
				if(indexEndConfiguration > index 
					&& rawPattern.length() > indexEndConfiguration
					&& (rawPattern.substring(indexEndConfiguration + 1).startsWith("static")))
				{
					String configurationString = rawPattern.substring(index + 2, indexEndConfiguration);
					return new ExtractedPosition(index, configurationString.length() + 9, new StaticValueElement(configurationString));
				}
			
		}
		
		
		return null;
	}
	
	@Override
	public String getFormattedValue(final JsonObject values) {
		
		return this.value;
		
	}
	

}
