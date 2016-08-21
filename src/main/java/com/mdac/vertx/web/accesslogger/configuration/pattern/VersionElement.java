package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Arrays;
import java.util.Map;

import com.mdac.vertx.web.accesslogger.util.VersionUtility;

public class VersionElement extends GenericAccessLogElement{

	public VersionElement(){
		super(Arrays.asList("%H"), null);
	}

	@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
	    return VersionUtility.getFormattedValue(values);
		
	}
	
	
	
}
