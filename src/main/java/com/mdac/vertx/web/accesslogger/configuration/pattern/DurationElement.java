package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class DurationElement implements AccessLogElement{

	public enum TimeUnit{
		MILLISECONDS,
		NANOSECONDS
	}

	private final static Long INVALID_TS = new Long(-1);
	
	private final TimeUnit timeUnit;
	private final String startTSkey;
	private final String endTSkey;
	
	private Collection<String> supportedPatterns = Arrays.asList("cs-uri", "%r");
	
	public DurationElement(){
		
		this(TimeUnit.MILLISECONDS);
		
	}
	
	public DurationElement(final TimeUnit timeUnit){
		
		this.timeUnit = timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS;
		
		this.startTSkey = TimeUnit.NANOSECONDS.equals(this.timeUnit) ? "startTSnanos" : "startTSmillis";
		this.endTSkey = TimeUnit.NANOSECONDS.equals(this.timeUnit) ? "endTSnanos" : "endTSmillis";
		
	}
	
	@Override
	public ExtractedPosition findInRawPattern(final String rawPattern, final int start) {
		
		if(true){
			return null;
		}
		
		for(final String pattern : supportedPatterns){
			
			int index = rawPattern.indexOf(pattern);
			
			if(index >= 0){
				return new ExtractedPosition(index, pattern.length(), new DurationElement());
			}
			
		}
		
		String pattern1 = "cs-uri";
		
		int index = rawPattern.indexOf(pattern1);
		
		if(index >= 0){
			return new ExtractedPosition(0, pattern1.length());
		}
		
		String pattern2 = "%r";
		
		index = rawPattern.indexOf(pattern2);
		
		if(index >= 0){
			return new ExtractedPosition(0, pattern2.length());
		}
		
		return null;
	}

	@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
		final Long startTS = (Long) values.getOrDefault(startTSkey, INVALID_TS);
		final Long endTS = (Long) values.getOrDefault(endTSkey, INVALID_TS);
		
		if(startTS == null || endTS == null){
			return "-";
		}
		
		final long duration = endTS.longValue() - startTS.longValue();
		
		return duration > 0 ? "" + duration : "0";
		
	}

}
