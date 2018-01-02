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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.BytesSentElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.DateTimeElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.DurationElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.HeaderElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.HostElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.MethodElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.RequestElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.StatusElement;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.VersionElement;

public class PatternResolver {

	// A list of all known access element implementations
	// Idea for the future is that this gets auto discovered somehow
	final Collection<AccessLogElement> availableElements = Arrays.asList(
																new RequestElement(),
																new DurationElement(),
																new StatusElement(),
																new MethodElement(),
																new VersionElement(),
																new DateTimeElement(),
																new BytesSentElement(),
																new HostElement(),
																new HeaderElement(),
																new CookieElement()
															);
	
	
	
	
	public ResolvedPatternResult resolvePattern(final String rawPattern){
		
		String rawPatternInEvaluation = rawPattern;
		final StringBuilder sbEvaluatedPattern = new StringBuilder();
		final Collection<AccessLogElement> logElements = new ArrayList<AccessLogElement>();
		
		while(rawPatternInEvaluation != null && rawPatternInEvaluation.length() > 0){
		
			int bestStart = -1;
			int bestOffset = 0;
			AccessLogElement bestElement = null;
			
			for(final AccessLogElement element : availableElements){
				
				final ExtractedPosition extractedPosition = element.findInRawPattern(rawPatternInEvaluation);
				
				if(extractedPosition == null || extractedPosition.getStart() == -1){
					continue;
				} else if (bestStart == -1 || extractedPosition.getStart() < bestStart){
					bestStart = extractedPosition.getStart();
					bestOffset = extractedPosition.getOffset();
					bestElement = extractedPosition.getElement();
				}
				
			}
			
			if(bestStart >= 0){
				
				if(bestStart > 0){
					// We need to take over some untranslatable part first
					sbEvaluatedPattern.append(rawPatternInEvaluation.substring(0, bestStart));
				}
				
				// Shorten the raw pattern till where we found replacement
				rawPatternInEvaluation = rawPatternInEvaluation.substring(bestStart + bestOffset);
				
				// Add the placeholder - for now always type string
				sbEvaluatedPattern.append("%s");
				
				// Add the log element
				logElements.add(bestElement);
				
			} else {
				// Looks like no more that can be resolved
				
				if(rawPatternInEvaluation != null && rawPatternInEvaluation.length() > 0){
					sbEvaluatedPattern.append(rawPatternInEvaluation);
				}
				
				break;
			}
			
		}
		
		return new ResolvedPatternResult(sbEvaluatedPattern.toString(), logElements);
	}
	

}
