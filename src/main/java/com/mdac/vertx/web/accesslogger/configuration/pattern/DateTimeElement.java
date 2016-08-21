package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.text.DateFormat;
import java.util.Map;

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
		
		final String requestPattern = "%t";
		int index = rawPattern.indexOf(requestPattern);
			
		if(index >= 0){
				
			if(start == -1 || index <= start)
			{
				return new ExtractedPosition(index, requestPattern.length(), new DateTimeElement(Utils.createRFC1123DateTimeFormatter()));
			}
		}
		
		
		return null;
	}

	@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append('[');
		
		sb.append(this.dateFormat.format(values.get("startTSmillis")));
		
		sb.append(']');
		
		return sb.toString();
	}

}
