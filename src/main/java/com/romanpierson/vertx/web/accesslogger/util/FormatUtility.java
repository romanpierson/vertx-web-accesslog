package com.romanpierson.vertx.web.accesslogger.util;

import io.vertx.core.json.JsonObject;

public class FormatUtility {

	private FormatUtility() {}
	
	public static String getStringOrNull(final JsonObject values, final String fieldName) {
		
		return values.getString(fieldName) != null ?  values.getString(fieldName) : null;
		
	}
	
	public static String getIntegerOrNull(final JsonObject values, final String fieldName) {
		
		return values.getInteger(fieldName) != null ?  Integer.toString(values.getInteger(fieldName)) : null;
		
	}
	
}
