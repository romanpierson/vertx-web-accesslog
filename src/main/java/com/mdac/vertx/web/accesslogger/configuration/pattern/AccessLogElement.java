package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Map;

public interface AccessLogElement {

	ExtractedPosition findInRawPattern(String rawPattern, int start);
	
	abstract String getFormattedValue(Map<String, Object> values);
	
}
