package com.mdac.vertx.web.accesslogger.util;

import java.util.Map;

import io.vertx.core.http.HttpVersion;

public class VersionUtility {

	private final static String EMPTY_VERSION = "-";
	
	public static String getFormattedValue(final Map<String, Object> values) {
		
		final HttpVersion version = values != null ? (HttpVersion) values.get("version") : null;
		
		if(!(version instanceof HttpVersion)){
			return EMPTY_VERSION;
		}
		
		final String versionFormatted;
		
	    switch (version){
	      case HTTP_1_0:
	        versionFormatted = "HTTP/1.0";
	        break;
	      case HTTP_1_1:
	        versionFormatted = "HTTP/1.1";
	        break;
		case HTTP_2:
			versionFormatted = "HTTP/2.0";
			break;
		default:
			versionFormatted = EMPTY_VERSION;
			break;
	    }
	    
	    return versionFormatted;
		
	}
}
