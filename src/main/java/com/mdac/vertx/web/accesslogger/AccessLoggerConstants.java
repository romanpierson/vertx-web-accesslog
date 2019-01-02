package com.mdac.vertx.web.accesslogger;

public interface AccessLoggerConstants {

	static final String EVENTBUS_EVENT_NAME = "accesslogevent";
	
	interface Request{
	
		interface Data{
			
			static enum Type{
				
				REMOTE_HOST("remoteHost"),
				LOCAL_HOST("localHost"),
				LOCAL_PORT("localPort"),
				START_TS_MILLIS("startTSmillis"),
				END_TS_MILLIS("endTSmillis"),
				BYTES_SENT("bytesSent"),
				URI("uri"),
				STATUS("status"),
				METHOD("method"),
				VERSION("version"),
				QUERY("query"),
				REQUEST_HEADERS("requestHeaders"),
				RESPONSE_HEADERS("responseHeaders"),
				COOKIES("cookies");
				
				private final String fieldName;
				
				private Type(String fieldName) {
					this.fieldName = fieldName;
				}
				
				public String getFieldName() {
					return this.fieldName;
				}
			}
			
			interface Fields{
				
				static final String COOKIE_NAME = "name";
				static final String COOKIE_VALUE = "value";
				
			}
			
		}
	}
	
}
