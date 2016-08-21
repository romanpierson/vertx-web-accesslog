package com.mdac.vertx.web.accesslogger.util;

import java.util.Map;

import io.vertx.core.http.HttpVersion;

public class VersionUtility {

	public static String getFormattedValue(final Map<String, Object> values) {
		
		final HttpVersion version = (HttpVersion) values.get("version");
		
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
			versionFormatted = "-";
			break;
	    }
	    
	    return versionFormatted;
		
	}
}
