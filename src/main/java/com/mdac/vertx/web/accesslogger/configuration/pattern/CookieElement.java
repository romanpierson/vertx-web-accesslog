package com.mdac.vertx.web.accesslogger.configuration.pattern;

import io.vertx.ext.web.Cookie;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CookieElement implements AccessLogElement{

	private final String identifier; 
	
	public CookieElement() {
		this.identifier = null;
	}
	
	private CookieElement(final String identifier) {
		this.identifier = identifier;
	}

	@Override
	public ExtractedPosition findInRawPattern(final String rawPattern, final int start) {
		
		final int index = rawPattern.indexOf("%{");
		
		if(index >= 0){
				
			if(start == -1 || index <= start)
			{
				int indexEndConfiguration = rawPattern.indexOf("}c");
			
				if(indexEndConfiguration > index)
				{
					String configurationString = rawPattern.substring(index + 2, indexEndConfiguration);
					
					return new ExtractedPosition(index, configurationString.length() + 4, new CookieElement(configurationString));
				}
			}
		}
		
		
		return null;
	}
	
	@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
		if(!values.containsKey("cookies")){
			return "-";
		}
		
		final Set<Cookie> cookies = (Set<Cookie>) values.get("cookies");
		
		final Optional<Cookie> cookie = cookies.stream().filter(c -> this.identifier.equals(c.getName())).findFirst();
		
		return cookie.isPresent() ? cookie.get().getValue() : "-";
		
	}

}
