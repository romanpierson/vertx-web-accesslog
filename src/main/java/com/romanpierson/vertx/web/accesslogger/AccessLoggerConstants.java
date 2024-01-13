/*
 * Copyright (c) 2016-2024 Roman Pierson
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
package com.romanpierson.vertx.web.accesslogger;

public class AccessLoggerConstants {

	private AccessLoggerConstants() {}
	
	public static final String EVENTBUS_RAW_EVENT_NAME = "accesslog.event.raw";
	public static final String EVENTBUS_APPENDER_EVENT_NAME = "accesslog.event.appender";
	public static final String EVENTBUS_REGISTER_EVENT_NAME = "accesslog.event.register";
	
	public static final String ZONE_UTC = "UTC";
	
	public static class HandlerConfiguration{
		
		private HandlerConfiguration() {}
		
		public static final String CONFIG_KEY_CONFIGURATIONS = "configurations";
		public static final String CONFIG_KEY_IDENTIFIER = "identifier";
		public static final String CONFIG_KEY_LOGPATTERN = "logPattern";
		public static final String CONFIG_KEY_APPENDERS = "appenders";
		public static final String CONFIG_KEY_APPENDER_CLASS_NAME = "appenderClassName";
		public static final String CONFIG_KEY_IS_AUTO_DEPLOY_PRODUCER_VERTICLE = "isAutoDeployProducerVerticle";
	}
	
	public static class InternalValues {
		
		private InternalValues() {}
		
		public static final String TIMESTAMP = "timestamp";
		
	}
	
	public static class Messages {
		
		private Messages() {}
		
		public static class Registration {
			
			private Registration() {}
			
			public static class Request {
			
				private Request() {}
				
				public static final String IDENTIFIER = "identifier";
				public static final String LOGPATTERN = "logPattern";
				public static final String APPENDERS = "appenders";
				public static final String APPENDER_CLASS_NAME = "appenderClassName";
				public static final String APPENDER_CONFIG = "config";
				
			}
			
			public static class Response {
				
				private Response() {}
				
				public static final String REQUIRES_COOKIES = "requiresCookies";
				public static final String REQUIRES_INCOMING_HEADERS = "requiresIncomingHeaders";
				public static final String REQUIRES_OUTGOING_HEADERS = "requiresOutgoingHeaders";
				public static final String RESULT = "result";
				public static final String RESULT_OK = "OK";
				public static final String RESULT_FAILED = "FAILED";
			}
		}
		
		public static class RawEvent {
			
			private RawEvent() {}
			
			public static class Request {
				
				private Request() {}
				
				public static final String IDENTIFIERS = "identifiers";
				
			}
			
		}
		
	}
	
	public static final String CONFIG_KEY_RESOLVED_PATTERN = "resolvedPattern";
	
	public static class Request{
	
		private Request() {}
		
		public static class Data{
			
			private Data() {}
			
			public enum Type{
				
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
			
			public static class Fields{
				
				private Fields() {}
				
				public static final String COOKIE_NAME = "name";
				public static final String COOKIE_VALUE = "value";
				
			}
			
		}
	}
	
	public static class ElasticSearchAppenderConfig{
		
		private ElasticSearchAppenderConfig() {}
		
		public static final String ELASTICSEARCH_INDEXER_EVENTBUS_EVENT_NAME = "es.indexer.event";
		
		public static class Field{
			
			private Field() {}
			
			public static final String TIMESTAMP = "timestamp";
			public static final String INSTANCE_IDENTIFIER = "instance_identifier";
			public static final String META = "meta";
			public static final String MESSAGE = "message";
			
		}
	}
	
	
}
