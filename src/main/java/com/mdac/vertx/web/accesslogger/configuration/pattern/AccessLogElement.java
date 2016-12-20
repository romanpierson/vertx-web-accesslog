package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Map;

public interface AccessLogElement {

	abstract ExtractedPosition findInRawPatternInternal(String rawPattern);
	
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
	
	abstract String getFormattedValue(Map<String, Object> values);
	
}
