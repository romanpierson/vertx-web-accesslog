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
import java.util.HashSet;
import java.util.Set;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants;
import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
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
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
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

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	private final EventBus eventBus;
	
	private final boolean requiresIncomingHeaders;
	private final boolean requiresOutgoingHeaders;
	private final boolean requiresCookies;
	
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
		
		final Set<Data.Type> requiredTypes = determinateRequiredElementData(logElements);
		
		this.requiresIncomingHeaders = requiredTypes.contains(Data.Type.REQUEST_HEADERS);
		this.requiresOutgoingHeaders = requiredTypes.contains(Data.Type.RESPONSE_HEADERS);
		this.requiresCookies = requiredTypes.contains(Data.Type.COOKIES);
		
		if(requiredTypes.isEmpty()) {
			LOG.info("No specific element data was claimed by access elements");
		} else {
			LOG.info("Specific element data for [{}] was claimed by access elements", requiredTypes);
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
	
	
	Set<Data.Type> determinateRequiredElementData(final Collection<AccessLogElement> logElements){
	
		final Set<Data.Type> requiredTypes = new HashSet<>();
		
		for(final AccessLogElement element : logElements) {
			requiredTypes.addAll(element.claimDataParts());
		}
		
		return requiredTypes;
		
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
										.put(Data.Type.START_TS_MILLIS.getFieldName(), startTSmillis)
										.put(Data.Type.END_TS_MILLIS.getFieldName(), System.currentTimeMillis())
										.put(Data.Type.STATUS.getFieldName(), response.getStatusCode())
										.put(Data.Type.METHOD.getFieldName(), request.method().name())
										.put(Data.Type.URI.getFieldName(), request.path())
										.put(Data.Type.VERSION.getFieldName(), request.version())
										.put(Data.Type.REMOTE_HOST.getFieldName(), request.remoteAddress().host())
										.put(Data.Type.LOCAL_HOST.getFieldName(), request.localAddress().host())
										.put(Data.Type.LOCAL_PORT.getFieldName(), request.localAddress().port());
		
		
		if(request.query() != null && !request.query().trim().isEmpty()){
			jsonValues.put(Data.Type.QUERY.getFieldName(), request.query());
		}
		
		if(response.bytesWritten() > 0){
			jsonValues.put(Data.Type.BYTES_SENT.getFieldName(), response.bytesWritten());
		}
		
		if(requiresIncomingHeaders) {
			jsonValues.put(Data.Type.REQUEST_HEADERS.getFieldName(), extractHeaders(request.headers()));
		}
		
		if(requiresOutgoingHeaders) {
			jsonValues.put(Data.Type.RESPONSE_HEADERS.getFieldName(), extractHeaders(response.headers()));
		}
		
		if(requiresCookies) {
			jsonValues.put(Data.Type.COOKIES.getFieldName(), extractCookies(context.cookies()));
		}
		
		eventBus.send(AccessLoggerConstants.EVENTBUS_EVENT_NAME, jsonValues);
		
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
			jsonArCookies.add(new JsonObject().put(Data.Fields.COOKIE_NAME, cookie.getName()).put(Data.Fields.COOKIE_VALUE, cookie.getValue()));
		}
		
		return jsonArCookies;
		
	}
}
