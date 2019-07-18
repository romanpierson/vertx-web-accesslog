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
package com.mdac.vertx.web.accesslogger.verticle;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants;
import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Messages.RawEvent;
import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Messages.Registration;
import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.mdac.vertx.web.accesslogger.appender.Appender;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.PatternResolver;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ResolvedPatternResult;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * Verticle that is responsible for
 * 
 * - Receiving and buffer access log meta data that arrives via the event bus 
 * - Produce output as configured
 * 
 * @author Roman Pierson
 *
 */
public class AccessLoggerProducerVerticle extends AbstractVerticle {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	
	final PatternResolver patternResolver = new PatternResolver();
	
	private Map<String, ResolvedLoggerConfiguration> resolvedLoggerConfigurations = new HashMap<>();
	
	@Override
	public void start() throws Exception {
 
		super.start();

		vertx.eventBus().<JsonObject> consumer(AccessLoggerConstants.EVENTBUS_RAW_EVENT_NAME, event -> {
			
			JsonArray identifiers = event.body().getJsonArray(RawEvent.Request.IDENTIFIERS);
			
			for (Object x : identifiers.getList()) {
				String identifier = (String) x;
				if(resolvedLoggerConfigurations.containsKey(identifier)) {
					
					JsonArray formatted = getFormattedValues(resolvedLoggerConfigurations.get(identifier).resolvedLogElements, event.body());
					
					for(Appender appender : resolvedLoggerConfigurations.get(identifier).rawAppender) {
						appender.push(formatted);
					}
				}	
			}
			
			
			
			/*identifiers.forEach(xIdentifier -> {
				String identifier = (String) xIdentifier;
				if(resolvedLoggerConfigurations.containsKey(identifier)) {
					
					JsonArray formatted = getFormattedValues(resolvedLoggerConfigurations.get(identifier).resolvedLogElements, event.body());
					
					for(Appender appender : resolvedLoggerConfigurations.get(identifier).rawAppender) {
						appender.push(formatted);
					}
				}
			});*/
			
		});
		
		vertx.eventBus().<JsonObject> consumer(AccessLoggerConstants.EVENTBUS_REGISTER_EVENT_NAME, event -> {
		
			event.reply(performRegistration(event.body()));
			
		});
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private synchronized JsonObject performRegistration(final JsonObject request) {
		
		String identifier = request.getString(Registration.Request.IDENTIFIER);
		String logPattern = request.getString(Registration.Request.LOGPATTERN);
		
		JsonObject response = new JsonObject();
		
		if(!resolvedLoggerConfigurations.containsKey(identifier)) {
			
			final ResolvedPatternResult result = patternResolver.resolvePattern(logPattern);
			
			ResolvedLoggerConfiguration config = new ResolvedLoggerConfiguration();
			config.originalLogPattern = logPattern;
			config.resolvedLogPattern = result.getResolvedPattern();
			config.resolvedLogElements = result.getLogElements();
				
			final Set<Data.Type> requiredTypes = determinateRequiredElementData(result.getLogElements());
			
			config.requiresIncomingHeaders = requiredTypes.contains(Data.Type.REQUEST_HEADERS);
			config.requiresOutgoingHeaders = requiredTypes.contains(Data.Type.RESPONSE_HEADERS);
			config.requiresCookies = requiredTypes.contains(Data.Type.COOKIES);
			
			JsonArray appenders = request.getJsonArray(Registration.Request.APPENDERS, new JsonArray());
			
			appenders.forEach(appender -> {
				
				JsonObject appenderConfig = (JsonObject) appender;
					
				JsonObject appenderConstructoreConfig = appenderConfig.getJsonObject(Registration.Request.APPENDER_CONFIG, new JsonObject());
				appenderConstructoreConfig.put(AccessLoggerConstants.CONFIG_KEY_RESOLVED_PATTERN, config.resolvedLogPattern);
					
				final String appenderClassName = appenderConfig.getString(Registration.Request.APPENDER_CLASS_NAME);
				try {
					Class clazz = this.getClass().getClassLoader().loadClass(appenderClassName);
					Appender appenderInstance = (Appender) clazz.getConstructor(JsonObject.class).newInstance(appenderConstructoreConfig);
					config.rawAppender.add(appenderInstance);
					
				}catch(Exception ex) {
					LOG.error("Failed to create appender with [{}]", ex, appenderClassName);
				}
				
			});
			
			resolvedLoggerConfigurations.put(identifier, config);
			
			LOG.info("Successfully created config for [{}]", identifier);
			
			if(config.requiresCookies || config.requiresIncomingHeaders || config.requiresOutgoingHeaders) {
				LOG.info("Config [{}] requires specific data for cookies [{}], incoming headers [{}], outgoing headers [{}]", identifier, config.requiresCookies, config.requiresIncomingHeaders, config.requiresOutgoingHeaders);
			} else {
				LOG.info("No specific data required for config [{}]", identifier);
			}
			
			populateResponse(response, config);
			
		} else if (resolvedLoggerConfigurations.get(identifier).originalLogPattern.equals(logPattern)) {
			
			LOG.info("Found and reused config for [{}]", identifier);
			
			populateResponse(response, resolvedLoggerConfigurations.get(identifier));
			
		} else {
			response.put(Registration.Response.RESULT, Registration.Response.RESULT_FAILED);
		}
		
		return response;
	}
	
	private void populateResponse(final JsonObject response, final ResolvedLoggerConfiguration config) {
		
		response.put(Registration.Response.RESULT, Registration.Response.RESULT_OK);
		response.put(Registration.Response.REQUIRES_COOKIES, config.requiresCookies);
		response.put(Registration.Response.REQUIRES_INCOMING_HEADERS, config.requiresIncomingHeaders);
		response.put(Registration.Response.REQUIRES_OUTGOING_HEADERS, config.requiresOutgoingHeaders);
		
	}
	
	private JsonArray getFormattedValues(final Collection<AccessLogElement>logElements,  final JsonObject rawValue) {
		
		JsonArray value = new JsonArray();
		
		for(final AccessLogElement alElement : logElements){
			final String formattedValue = alElement.getFormattedValue(rawValue);
			value.add(formattedValue != null ? formattedValue : "");
		}
		
		return value;
		
	}

	@Override
	public void stop() throws Exception {

		LOG.info("Stopping AccessLoggerProducerVerticle");
		
		LOG.info("Notifying raw appenders about shutdown");
		this.resolvedLoggerConfigurations.values().forEach(resolvedLoggerConfiguration -> {
			resolvedLoggerConfiguration.rawAppender.forEach(rawAppender -> rawAppender.notifyShutdown());
		});
		
		super.stop();

	}

	Set<Data.Type> determinateRequiredElementData(final Collection<AccessLogElement> logElements){
		
		final Set<Data.Type> requiredTypes = new HashSet<>();
		
		for(final AccessLogElement element : logElements) {
			requiredTypes.addAll(element.claimDataParts());
		}
		
		return requiredTypes;
		
	}
}
