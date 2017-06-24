/*
 * Copyright (c) 2016-2017 Roman Pierson
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

import java.util.Map;

public class VersionUtility {

	private final static String EMPTY_VERSION = "-";
	
	public static String getFormattedValue(final Map<String, Object> values) {
		
		final String version = values != null ? (String) values.get("version") : null;
		
		if(version == null){
			return EMPTY_VERSION;
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
			versionFormatted = EMPTY_VERSION;
			break;
	    }
	    
	    return versionFormatted;
		
	}
}
