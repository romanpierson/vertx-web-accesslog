package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.vertx.ext.web.impl.Utils;

public class DateTimeElement implements AccessLogElement{

	private final DateFormat dateFormat;
	
	public DateTimeElement() {
		this.dateFormat = null;
	}
	
	private DateTimeElement(final DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public ExtractedPosition findInRawPattern(final String rawPattern, final int start) {
		
		// Check if we have a configured datetime element
		int index = rawPattern.indexOf("%{");
		
		if(index >= 0){
				
			if(start == -1 || index <= start)
			{
				int indexEndConfigurationDatetime = rawPattern.indexOf("}t");
				int indexEndConfiguration = rawPattern.indexOf("}");
			
				if(indexEndConfigurationDatetime > index && (indexEndConfigurationDatetime == indexEndConfiguration)){
					String configurationString = rawPattern.substring(index + 2, indexEndConfigurationDatetime);
					System.out.println("(" + configurationString + ")");
					
					return new ExtractedPosition(index, configurationString.length() + 4, new DateTimeElement(deriveDateFormatFromConfigurationString(configurationString)));
				}
			}
		}
		
		// Check if we have an unconfigured element
		final String requestPattern = "%t";
		index = rawPattern.indexOf(requestPattern);
			
		if(index >= 0){
				
			if(start == -1 || index <= start)
			{
				
				return new ExtractedPosition(index, requestPattern.length(), new DateTimeElement(Utils.createRFC1123DateTimeFormatter()));
			}
		}
		
		
		
		return null;
	}
	
	protected DateFormat deriveDateFormatFromConfigurationString(final String configurationString){
		
		if(configurationString != null && configurationString.length() > 0){
			
			final String[] configurationTokens = configurationString.split("\\|");
			
			if(configurationTokens != null && configurationTokens.length == 3){
			
				// Assume that is a configuration including format, timezone and locale
				
				DateFormat dtf = new SimpleDateFormat(configurationTokens[0], Locale.forLanguageTag(configurationTokens[2]));
				dtf.setTimeZone(TimeZone.getTimeZone(configurationTokens[1]));
				
				return dtf;
			} 
			else {
				
				// Assume this is just a format configuration
				DateFormat dtf = new SimpleDateFormat(configurationTokens[0], Locale.ENGLISH);
				dtf.setTimeZone(TimeZone.getTimeZone("GMT"));
				
			}
		}
		
		return Utils.createRFC1123DateTimeFormatter();
		
	}

	@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append(this.dateFormat.format(values.get("startTSmillis")));
		
		return sb.toString();
	}

}
