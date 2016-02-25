package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Map;

public interface AccessLogElement {

	abstract ExtractedPosition findInRawPattern(String rawPattern);
	
	abstract String getFormattedValue(Map<String, Object> values);
	
}
