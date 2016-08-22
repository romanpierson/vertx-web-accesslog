package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Map;

public class HostElement implements AccessLogElement{

	private final Mode mode;
	
	private enum Mode{
		
		REMOTE_HOST,	
		LOCAL_HOST,
		LOCAL_PORT
		
	}
	
	public HostElement(){
		
		this.mode = null;
		
	}
	
	private HostElement(final Mode mode){
		
		this.mode = mode;
		
	}
	
	@Override
	public ExtractedPosition findInRawPattern(final String rawPattern, final int start) {
		
		ExtractedPosition foundPosition = null;
		
		String patternRemoteHost = "%h";
		
		int index = rawPattern.indexOf(patternRemoteHost);
		
		if(index >= 0){
			
			if(start == -1 || index <= start)
			{
				foundPosition = new ExtractedPosition(index, patternRemoteHost.length(), new HostElement(Mode.REMOTE_HOST));
			}
		}
		
		String patternLocalHost = "%v";
		
		index = rawPattern.indexOf(patternLocalHost);
		
		if(index >= 0){
			
			if(start == -1 || index <= start)
			{
				if(foundPosition == null || index < foundPosition.getStart()){
					foundPosition = new ExtractedPosition(index, patternLocalHost.length(), new HostElement(Mode.LOCAL_HOST));
				}
			}
		}
		
		String patternLocalPort = "%p";
		
		index = rawPattern.indexOf(patternLocalPort);
		
		if(index >= 0){
			
			if(start == -1 || index <= start)
			{
				if(foundPosition == null || index < foundPosition.getStart()){
					foundPosition = new ExtractedPosition(index, patternLocalPort.length(), new HostElement(Mode.LOCAL_PORT));
				}
			}
		}
		
		return foundPosition;
	}
	
	@Override
	public String getFormattedValue(final Map<String, Object> values) {
	
		switch (this.mode){
		
			case LOCAL_HOST:
				return "" + values.get("localHost");
			case LOCAL_PORT:
				return "" + values.get("localPort");
			case REMOTE_HOST:
				return "" + values.get("remoteHost");
			default:
				break;
			
		}
		
		return "-";
	}
	
}
