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
package com.mdac.vertx.web.accesslogger.impl;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.mdac.vertx.web.accesslogger.AccessLoggerHandler;
import com.mdac.vertx.web.accesslogger.appender.Appender;
import com.mdac.vertx.web.accesslogger.appender.AppenderOptions;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.PatternResolver;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ResolvedPatternResult;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * Access Logger for requests
 * 
 * @author Roman Pierson
 *
 */
public class AccessLoggerHandlerImpl implements AccessLoggerHandler {

	private final EventBus eventBus;
	
	@SuppressWarnings("rawtypes")
	public AccessLoggerHandlerImpl(final AccessLoggerOptions accessLoggerOptions, final Collection<AppenderOptions> appenderOptions) {
		
		if(accessLoggerOptions == null || appenderOptions == null || appenderOptions.size() == 0){
			throw new IllegalArgumentException("must specify at least one accessLoggerOptions and one appenderOptions");
		}
		
		if(accessLoggerOptions.getPattern() != null && accessLoggerOptions.getLogElements().size() > 1){
			throw new IllegalArgumentException("must not specify a pattern and logElements");
		}
		
		String resolvedPattern = null;
		Collection<AccessLogElement> logElements;
		
		if(accessLoggerOptions.getPattern() != null){
			// A pattern was defined
			final ResolvedPatternResult resolvedPatternResult = new PatternResolver().resolvePattern(accessLoggerOptions.getPattern());
			
			resolvedPattern = resolvedPatternResult.getResolvedPattern();
			logElements = resolvedPatternResult.getLogElements();
			
		} else {
			// Log elements were defined
			throw new UnsupportedOperationException();
		}
		
		// Create the appenders
		Collection<Appender> appenders = new ArrayList<>(appenderOptions.size());
		
		for(final AppenderOptions appenderOption : appenderOptions){
			
			final String appenderImplementationClassName = appenderOption.getAppenderImplementationClassName();
			
			if(appenderImplementationClassName == null || appenderImplementationClassName.trim().isEmpty()){
				throw new IllegalArgumentException("must not specify an appender with empty appenderImplementationClass");
			}
			
			Class appenderClass = null;
			
			try{
				appenderClass = Class.forName(appenderImplementationClassName);
			}catch(Exception ex){
				throw new IllegalArgumentException("appenderImplementationClass [" + appenderImplementationClassName + "] was not found", ex);
			}
			
			Constructor constructor = null;
			
			try{
				
				Constructor[] constructors = appenderClass.getConstructors();
				
				if(constructors.length != 1){
					throw new IllegalArgumentException("appenderImplementationClass [" + appenderImplementationClassName + "] must specify exactly one constructor");
				}
				
				constructor = constructors[0];
				
			}catch(Exception ex){
				throw new IllegalArgumentException("could not look up constructor", ex);
			}
			
			
			Appender appender = null;
			
			try{
				appender = (Appender) constructor.newInstance(appenderOption, logElements);
			}catch(Exception ex){
				throw new IllegalArgumentException("Failed to instantiate appenderImplementationClass of type [" + appenderImplementationClassName + "]", ex);
			}
			
			if(appender.requiresResolvedPattern()){
				appender.setResolvedPattern(resolvedPattern);
			}
			
			appenders.add(appender);
			
		}
		
		Vertx.currentContext().owner().deployVerticle(new AccessLoggerProducerVerticle(accessLoggerOptions, appenders), new DeploymentOptions().setWorker(true));
		
		eventBus = Vertx.currentContext().owner().eventBus();
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
		
		JsonObject jsonValues = new JsonObject()
										.put("startTSmillis", startTSmillis)
										.put("endTSmillis", System.currentTimeMillis())
										.put("status", response.getStatusCode())
										.put("method", request.method().name())
										.put("uri", request.path())
										.put("version", request.version())
										.put("remoteHost", request.remoteAddress().host())
										.put("localHost", request.localAddress().host())
										.put("localPort", request.localAddress().port());
		
		
		if(request.query() != null && !request.query().trim().isEmpty()){
			jsonValues.put("query", request.query());
		}
		
		if(response.bytesWritten() > 0){
			jsonValues.put("bytesSent", response.bytesWritten());
		}
		
		jsonValues.put("requestHeaders", extractHeaders(request.headers()));
		jsonValues.put("responseHeaders", extractHeaders(response.headers()));
		
		jsonValues.put("cookies", extractCookies(context.cookies()));
		
		eventBus.send("accesslogevent", jsonValues);
		
	}
	
	private JsonObject extractHeaders(final MultiMap headersMap){
		
		JsonObject headers = new JsonObject();
		headersMap.forEach(entry -> {
			headers.put(entry.getKey(), entry.getValue());
		});
		
		return headers;
		
	}
	
	private JsonArray extractCookies(final Set<Cookie> cookies) {
		
		JsonArray jsonArCookies = new JsonArray();
		
		for(final Cookie cookie : cookies) {
			jsonArCookies.add(new JsonObject().put("name", cookie.getName()).put("value", cookie.getValue()));
		}
		
		return jsonArCookies;
		
	}
}
