package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GenericAccessLogElement implements AccessLogElement {

	private final Collection<String> supportedPatterns;
	private final String valueIdentifier;
	
	protected GenericAccessLogElement(final Collection<String> supportedPatterns,
			                          final String valueIdentifier){
		
		this.supportedPatterns = supportedPatterns != null ? supportedPatterns : new ArrayList<String>();
		this.valueIdentifier = valueIdentifier;
		
	}
	
	@Override
	public ExtractedPosition findInRawPattern(final String rawPattern, final int start) {
		
		for(final String supportedPattern : this.supportedPatterns){
			
			int index = rawPattern.indexOf(supportedPattern);
			
			if(index >= 0){
				
				if(start == -1
					|| index <= start)
				{
					final AccessLogElement logElement = createElementInstance();
					
					if(logElement != null){
						return new ExtractedPosition(index, supportedPattern.length(), logElement);
					} 
				}
			}
		}
		
		return null;
	}

	@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
		return "" + values.get(this.valueIdentifier);
		
	}
	
	protected AccessLogElement createElementInstance(){
		
		try {
			return this.getClass().newInstance();
		} catch (final Exception ex) {
			
		} 
		
		return null;
		
	}
}
