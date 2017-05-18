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
package com.mdac.vertx.web.accesslogger.impl;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mdac.vertx.web.accesslogger.AccessLoggerHandler;
import com.mdac.vertx.web.accesslogger.configuration.output.OutputConfiguration;
import com.mdac.vertx.web.accesslogger.configuration.pattern.PatternResolver;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ResolvedPatternResult;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.Utils;

/**
 * 
 * Access Logger for requests
 * 
 * @author Roman Pierson
 *
 */
@SuppressWarnings("unused")
public class AccessLoggerHandlerImpl implements AccessLoggerHandler {

	private final DateFormat dateTimeFormat = Utils.createRFC1123DateTimeFormatter();

	private Logger logger = LoggerFactory.getLogger(AccessLoggerHandlerImpl.class);
	
	private OutputConfiguration outputConfiguration;
	
	private PatternResolver patternResolver = new PatternResolver();
	
	public AccessLoggerHandlerImpl(final String pattern) {
		
		final ResolvedPatternResult resolvedPattern = patternResolver.resolvePattern(pattern);
		
		if(resolvedPattern != null){
			outputConfiguration = new OutputConfiguration(resolvedPattern.getResolvedPattern(), 
					resolvedPattern.getLogElements(), 
					Arrays.asList(logger));
		}
		
	}
	
	
	@Override
	public void handle(final RoutingContext context) {
		
		long startTSmillis = System.currentTimeMillis();
		
		context.addBodyEndHandler(v -> log(context, startTSmillis));
		
		context.next();
		
	}
	
	private void log(final RoutingContext context, long startTSmillis){
		
		final HttpServerRequest request = context.request();
		final HttpServerResponse response = context.response();
		
		final Map<String, Object> values = new HashMap<String, Object>();
		values.put("uri", request.path());
		if(request.query() != null){
			values.put("query", request.query());
		}
		values.put("method", request.method());
		values.put("status", response.getStatusCode());
		values.put("startTSmillis", startTSmillis);
		values.put("endTSmillis", System.currentTimeMillis());
		values.put("version", request.version());
		if(response.bytesWritten() > 0){
			values.put("bytesSent", response.bytesWritten());
		}
		
		values.put("remoteHost", request.remoteAddress().host());
		values.put("localHost", request.localAddress().host());
		values.put("localPort", request.localAddress().port());
		
		values.put("requestHeaders", request.headers());
		values.put("responseHeaders", response.headers());
		
		if(context.cookieCount() > 0){
			values.put("cookies", context.cookies());
		}
		
		outputConfiguration.doLog(values);
		
	}
	
}
