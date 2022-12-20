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
package com.romanpierson.vertx.web.accesslogger.configuration.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;

public class PatternResolver {

	final Collection<AccessLogElement> availableElements = new ArrayList<>(); 
	
	public PatternResolver() {

		ServiceLoader<AccessLogElement> loader = ServiceLoader.load(AccessLogElement.class);

		loader.forEach(availableElements::add);

	}
	
	public ResolvedPatternResult resolvePattern(final String rawPattern){
		
		String rawPatternInEvaluation = rawPattern;
		final StringBuilder sbEvaluatedPattern = new StringBuilder();
		final Collection<AccessLogElement> logElements = new ArrayList<>();
		
		while(rawPatternInEvaluation != null && rawPatternInEvaluation.length() > 0){
		
			int bestStart = -1;
			int bestOffset = 0;
			AccessLogElement bestElement = null;
			
			for(final AccessLogElement element : availableElements){
				
				final ExtractedPosition extractedPosition = element.findInRawPattern(rawPatternInEvaluation);
				
				if(extractedPosition == null || extractedPosition.getStart() == -1){
					continue;
				} else if (bestStart == -1 || extractedPosition.getStart() <= bestStart){
					if(extractedPosition.getStart() == bestStart  && extractedPosition.getOffset() >= bestOffset) {
						// Trying to retain some of the logic
						// If the start position is equal then we must have a duplicate pattern use the item with the shorter length
						continue;
					}

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
