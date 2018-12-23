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

import java.util.ArrayList;
import java.util.Collection;

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class GenericAccessLogElement implements AccessLogElement {

	private final Collection<String> supportedPatterns;
	private final String valueIdentifier;
	
	protected GenericAccessLogElement(final Collection<String> supportedPatterns,
			                          final String valueIdentifier){
		
		this.supportedPatterns = supportedPatterns != null ? supportedPatterns : new ArrayList<String>();
		this.valueIdentifier = valueIdentifier;
		
	}
	
	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		for(final String supportedPattern : this.supportedPatterns){
			
			int index = rawPattern.indexOf(supportedPattern);
			
			if(index >= 0){
				
					final AccessLogElement logElement = createElementInstance();
					
					if(logElement != null){
						return new ExtractedPosition(index, supportedPattern.length(), logElement);
					} 
				
			}
		}
		
		return null;
	}

	@Override
	public String getFormattedValue(final JsonObject values) {
		
		return "" + values.getValue(this.valueIdentifier);
	}

	protected AccessLogElement createElementInstance(){
		
		try {
			return this.getClass().newInstance();
		} catch (final Exception ex) {
			
		} 
		
		return null;
		
	}
}
