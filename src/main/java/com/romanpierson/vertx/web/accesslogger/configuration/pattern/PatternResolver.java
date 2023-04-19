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
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Supplier;

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

public class PatternResolver {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
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
		
			ExtractedPosition currentBestMatchingElement = null;
			
			for(final AccessLogElement element : availableElements){
				
				final Collection<ExtractedPosition> matchingElements = element.findInRawPattern(rawPatternInEvaluation);
				
				for(final ExtractedPosition matchingElement : matchingElements) {
				
					if (currentBestMatchingElement == null) {
						
						// First element that matches at all - take it
						currentBestMatchingElement = matchingElement;
						
					} else {
						// There is already a current best matching element so we need to check if this one is "better"
						if(matchingElement.getStart() < currentBestMatchingElement.getStart()) {
							
							// The new element starts earlier so should be a better match
							currentBestMatchingElement = matchingElement;
							
						} else if(matchingElement.getStart() == currentBestMatchingElement.getStart()) {
							
							// Both starts at same 
							if(matchingElement.getOffset() > currentBestMatchingElement.getOffset()) {
								
								// Trying to retain some of the logic
								// If the start position is equal then we must have a duplicate pattern use the item with the shorter length
								continue;
								
							} else if (matchingElement.getOffset() == currentBestMatchingElement.getOffset()) {
								
								// All identical - we give priority to non default elements to allow overwriting same pattern with custom elements
								if(matchingElement.isCustom() && !currentBestMatchingElement.isCustom()) {
									
									currentBestMatchingElement = matchingElement;
									
								} else if ((matchingElement.isCustom() && currentBestMatchingElement.isCustom()) || (!matchingElement.isCustom() && !currentBestMatchingElement.isCustom())) {
									
									// warn as both are same 
									logger.warn("Found two elements that have identical match - first found will be used");
								}
								
								// If we get here means we got a no custom element and already custom element is set - so we can ignore
							}
						}
						
					}
				}
			}
			
			if(currentBestMatchingElement != null){
				
				if(currentBestMatchingElement.getStart() > 0){
					// We need to take over some untranslatable part first
					sbEvaluatedPattern.append(rawPatternInEvaluation.substring(0, currentBestMatchingElement.getStart()));
				}
				
				// Shorten the raw pattern till where we found replacement
				rawPatternInEvaluation = rawPatternInEvaluation.substring(currentBestMatchingElement.getStart() + currentBestMatchingElement.getOffset());
				
				// Add the placeholder - for now always type string
				sbEvaluatedPattern.append("%s");
				
				// Add the log element
				if(currentBestMatchingElement.getElementFunction() != null) {
					logElements.add(currentBestMatchingElement.getElementFunction().apply(currentBestMatchingElement.getConfig()));
				}else {
					logElements.add(currentBestMatchingElement.getElementSupplier().get());
				}
				
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
	
	public static Optional<ExtractedPosition> extractBestPositionFromPostfixPatternIfApplicable(
			final String rawPattern, 
			final String postfixPattern, 
			final Function<JsonObject, AccessLogElement> logElementFunction) {

		return extractBestPositionFromPostfixPatternAndAdditionalCheckIfApplicable(rawPattern, postfixPattern, 
				json -> Boolean.TRUE,
				logElementFunction);
		
	}
	
	public static Optional<ExtractedPosition> extractBestPositionFromPostfixPatternAndAdditionalCheckIfApplicable(
										final String rawPattern, 
										final String postfixPattern, 
										final Function<JsonObject, Boolean> matchElementFunction, 
										final Function<JsonObject, AccessLogElement> logElementFunction) {
		
		final int index = rawPattern.indexOf("%{");
		
		if(index >= 0){
				
			int indexEndConfiguration = rawPattern.indexOf('}');
			
			if(indexEndConfiguration > index 
				&& rawPattern.length() > indexEndConfiguration
				&& (rawPattern.substring(indexEndConfiguration + 1).startsWith(postfixPattern)))
			{
				String configurationString = rawPattern.substring(index + 2, indexEndConfiguration);
				
				final JsonObject configurationJson = JsonObject.of("configuration", configurationString);
				
				// Check if the potential element is able to support the provided configuration
				
				if(Boolean.TRUE.equals(matchElementFunction.apply(configurationJson))){
				
					return Optional.of(
							ExtractedPosition.build(
								index, 
								configurationString.length() + 3 + postfixPattern.length(), 
								logElementFunction,
								configurationJson
								));
					
				}
			}
		}
		
		return Optional.empty();
		
	}
	
	public static Optional<ExtractedPosition> extractBestPositionFromFixPatternIfApplicable(final String rawPattern, final String fixPattern, Supplier<AccessLogElement> logElementSupplier) {
		
		int index = rawPattern.indexOf(fixPattern);
		
		if(index >= 0){
			return Optional.of(ExtractedPosition.build(index, fixPattern.length(), logElementSupplier));
		}
		
		return Optional.empty();
		
	}
	
	public static Optional<Collection<ExtractedPosition>> extractBestPositionFromFixPatternsIfApplicable(final String rawPattern, final Collection<String> fixPatterns, Supplier<AccessLogElement> logElementSupplier) {
		
		final Collection<ExtractedPosition> foundPositions = new ArrayList<>(fixPatterns.size());
		
		fixPatterns.forEach(fixPattern -> {
			
			int index = rawPattern.indexOf(fixPattern);
			
			if(index >= 0){
				foundPositions.add(ExtractedPosition.build(index, fixPattern.length(), logElementSupplier));
			}
		});
		
		return foundPositions.isEmpty() ? Optional.empty() : Optional.of(foundPositions);
		
	}

}
