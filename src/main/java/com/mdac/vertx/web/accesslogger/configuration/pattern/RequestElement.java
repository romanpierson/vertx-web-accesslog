package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class RequestElement implements AccessLogElement{

	private Collection<String> supportedPatterns = Arrays.asList("cs-uri", "%r");
	
	@Override
	public ExtractedPosition findInRawPattern(final String rawPattern, final int start) {
		
		for(final String pattern : supportedPatterns){
			
			int index = rawPattern.indexOf(pattern);
			
			if(index >= 0){
				
				if(start == -1
					|| index <= start)
				{
					return new ExtractedPosition(index, pattern.length(), new RequestElement());
				}
			}
		}
		
		return null;
	}

	@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
		return (String) values.get("uri");
		
	}

}
