package com.mdac.vertx.web.accesslogger;

public interface AccessLoggerConstants {

	static final String EVENTBUS_RAW_EVENT_NAME = "accesslog.event.raw";
	static final String EVENTBUS_APPENDER_EVENT_NAME = "accesslog.event.appender";
	static final String EVENTBUS_REGISTER_EVENT_NAME = "accesslog.event.register";
	
	static final String ZONE_UTC = "UTC";
	
	interface HandlerConfiguration{
		
		static final String CONFIG_KEY_CONFIGURATIONS = "configurations";
		static final String CONFIG_KEY_IDENTIFIER = "identifier";
		static final String CONFIG_KEY_LOGPATTERN = "logPattern";
		static final String CONFIG_KEY_APPENDERS = "appenders";
		static final String CONFIG_KEY_APPENDER_CLASS_NAME = "appenderClassName";
		static final String CONFIG_KEY_IS_AUTO_DEPLOY_PRODUCER_VERTICLE = "isAutoDeployProducerVerticle";
	}
	
	interface Messages {
		
		interface Registration {
			
			interface Request {
			
				static final String IDENTIFIER = "identifier";
				static final String LOGPATTERN = "logPattern";
				static final String APPENDERS = "appenders";
				static final String APPENDER_CLASS_NAME = "appenderClassName";
				static final String APPENDER_CONFIG = "config";
				
			}
			
			interface Response {
				
				static final String REQUIRES_COOKIES = "requiresCookies";
				static final String REQUIRES_INCOMING_HEADERS = "requiresIncomingHeaders";
				static final String REQUIRES_OUTGOING_HEADERS = "requiresOutgoingHeaders";
				static final String RESULT = "result";
				static final String RESULT_OK = "OK";
				static final String RESULT_FAILED = "FAILED";
			}
		}
		
		interface RawEvent {
			
			interface Request {
				
				static final String IDENTIFIERS = "identifiers";
				
			}
			
		}
		
	}
	
	
	
	
	static final String CONFIG_KEY_RESOLVED_PATTERN = "resolvedPattern";
	
	interface Request{
	
		interface Data{
			
			enum Type{
				
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
