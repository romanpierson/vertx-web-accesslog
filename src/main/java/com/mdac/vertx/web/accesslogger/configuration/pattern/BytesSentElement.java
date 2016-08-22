package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Map;

public class BytesSentElement implements AccessLogElement{

	private final Mode mode;
	
	private enum Mode{
		
		NO_BYTES_NULL,	
		NO_BYTES_DASH
		
	}
	
	public BytesSentElement(){
		
		this.mode = null;
		
	}
	
	private BytesSentElement(final Mode mode){
		
		this.mode = mode;
		
	}
	
	@Override
	public ExtractedPosition findInRawPattern(final String rawPattern, final int start) {
		
		String patternDash = "%b";
		
		int index = rawPattern.indexOf(patternDash);
		
		if(index >= 0){
			
			if(start == -1
				|| index <= start)
			{
				return new ExtractedPosition(index, patternDash.length(), new BytesSentElement(Mode.NO_BYTES_DASH));
			}
		}
		
		String patternNull = "%B";
		
		index = rawPattern.indexOf(patternNull);
		
		if(index >= 0){
			
			if(start == -1
				|| index <= start)
			{
				return new ExtractedPosition(index, patternNull.length(), new BytesSentElement(Mode.NO_BYTES_NULL));
			}
		}
		
		return null;
	}
	
	@Override
	public String getFormattedValue(final Map<String, Object> values) {
	
		if(values.containsKey("bytesSent")){
			
			return "" + values.get("bytesSent");
			
		} else {
			
			return Mode.NO_BYTES_DASH.equals(this.mode) ? "-" : "0";
			
		}
		
	}
	
}
