/*
 * Copyright (c) 2016-2019 Roman Pierson
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 
 * which accompanies this distribution.
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package com.mdac.vertx.web.accesslogger.util;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;

import io.vertx.core.json.JsonObject;

public class VersionUtility {

	private VersionUtility() {}
	
	public static String getFormattedValue(final JsonObject values) {
		
		final String version = values.getString(Data.Type.VERSION.getFieldName(), null);
		
		return transform(version);
		
	}
	
	private static String transform(final String version){
		
		if(version == null || version.trim().isEmpty()){
			return null;
		}
		
		final String versionFormatted;
		
	    switch (version){
	      case "HTTP_1_0":
	        versionFormatted = "HTTP/1.0";
	        break;
	      case "HTTP_1_1":
	        versionFormatted = "HTTP/1.1";
	        break;
		case "HTTP_2":
			versionFormatted = "HTTP/2.0";
			break;
		default:
			versionFormatted = version;
			break;
	    }
	    
	    return versionFormatted;
		
	}
	
}
