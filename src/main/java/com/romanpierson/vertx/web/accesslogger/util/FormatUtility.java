package com.romanpierson.vertx.web.accesslogger.util;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;

import io.vertx.core.json.JsonObject;

public class FormatUtility {

	private final static JsonObject EMPTY_JSON_OBJECT = new JsonObject();
	
	private FormatUtility() {}
	
	public static String getStringOrNull(final JsonObject values, final String fieldName) {
		
		return values.getString(fieldName) != null ?  values.getString(fieldName) : null;
		
	}
	
	public static String getIntegerOrNull(final JsonObject values, final String fieldName) {
		
		return values.getInteger(fieldName) != null ?  Integer.toString(values.getInteger(fieldName)) : null;
		
	}
	
	public static String getHostFromHeaderOrNull(final JsonObject values) {
		
		 final String hostHeaderValue = values.getJsonObject(Data.Type.REQUEST_HEADERS.getFieldName(), EMPTY_JSON_OBJECT).getString("host", null);
		 
		 int portDivider = hostHeaderValue == null ? -1 : hostHeaderValue.indexOf(':');
		 
		 if(portDivider >= 0) {
			 return hostHeaderValue.substring(0, portDivider);
		 } else {
			 return hostHeaderValue;
		 }
		 
	}
	
}
