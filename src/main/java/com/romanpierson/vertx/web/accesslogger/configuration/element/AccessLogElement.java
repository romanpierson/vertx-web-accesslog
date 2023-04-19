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
package com.romanpierson.vertx.web.accesslogger.configuration.element;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public interface AccessLogElement {

	
	Collection<ExtractedPosition> findInRawPatternInternal(String rawPattern);
	
	/**
	 * 
	 * 
	 * 
	 * @param rawPattern		The pattern you want to search on
	 * @return					A list of matching extracted positions
	 */
	default Collection<ExtractedPosition> findInRawPattern(final String rawPattern){
		
		final Collection<ExtractedPosition> foundPatterns = findInRawPatternInternal(rawPattern);
		
		for(final ExtractedPosition ep : foundPatterns) {
			if(ep == null || ep.getStart() == -1){
				// at least one element is invalid for further usage - so we will clean the elements
				return foundPatterns.stream().filter(Objects::nonNull).filter(ep2 -> ep2.getStart() >= 0).collect(Collectors.toList());
			}
		}
		
		// All elements clean when we get here
		return foundPatterns;
		
	}
	
	/**
	 * 
	 * Allows to communicate what parts of the request are required for this element
	 * 
	 * Only required for the more complex datasets - the basic data parts are always provided
	 * 
	 * @return	The list of explicitly required data parts
	 */
	default Collection<AccessLoggerConstants.Request.Data.Type> claimDataParts(){
		
		// Not enforcing anymore element implementations to explicitly define this lookup functionality 
		return Collections.emptyList();
		
	}

	abstract String getFormattedValue(JsonObject values);
	
	
	
	
}
