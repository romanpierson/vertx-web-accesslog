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
package com.mdac.vertx.web.accesslogger.configuration.element;

import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public interface AccessLogElement {

	
	default ExtractedPosition findInRawPatternInternal(String rawPattern){
		
		// Not enforcing anymore element implementations to explicitly define this lookup functionality 
		return null;
		
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param rawPattern		The pattern you want to search on
	 * @return					A matching extracted position or NULL
	 */
	default ExtractedPosition findInRawPattern(final String rawPattern){
		
		if(rawPattern == null || rawPattern.trim().length() == 0){
			throw new IllegalArgumentException("Parameter rawPattern must not be empty");
		}
		
		return findInRawPatternInternal(rawPattern);
		
	}
	
	abstract String getFormattedValue(JsonObject values);
	
}
