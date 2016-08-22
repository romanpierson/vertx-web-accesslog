package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PatternResolver {

	// A list of all known access element implementations
	// Idea for the future is that this gets auto discovered somehow
	final Collection<AccessLogElement> availableElements = Arrays.asList(
																new RequestElement(),
																new DurationElement(),
																new StatusElement(),
																new MethodElement(),
																new VersionElement(),
																new DateTimeElement()
															);
	
	
	
	
	public ResolvedPatternResult resolvePattern(final String rawPattern){
		
		//System.out.println("rawPattern:" + rawPattern + "|");
		
		String rawPatternInEvaluation = rawPattern;
		final StringBuilder sbEvaluatedPattern = new StringBuilder();
		final Collection<AccessLogElement> logElements = new ArrayList<AccessLogElement>();
		
		while(rawPatternInEvaluation != null && rawPatternInEvaluation.length() > 0){
		
			int bestStart = -1;
			int bestOffset = 0;
			AccessLogElement bestElement = null;
			
			for(final AccessLogElement element : availableElements){
				
				final ExtractedPosition extractedPosition = element.findInRawPattern(rawPatternInEvaluation, bestStart);
				
				//System.out.println(extractedPosition.getStart() + "/" + extractedPosition.getOffset());
				
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
				
				//System.out.println("|" + rawPatternInEvaluation + "|");
				
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
		
		//System.out.println("sbEvaluatedPattern:" + sbEvaluatedPattern.toString() + "|");
		
		return new ResolvedPatternResult(sbEvaluatedPattern.toString(), logElements);
	}
	

}
