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
package com.romanpierson.vertx.web.accesslogger.verticle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants;
import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Messages.RawEvent;
import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Messages.Registration;
import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.appender.Appender;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.PatternResolver;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ResolvedPatternResult;
import com.romanpierson.vertx.web.accesslogger.exception.AccessLoggerException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
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

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	final PatternResolver patternResolver = new PatternResolver();

	private Map<String, ResolvedLoggerConfiguration> resolvedLoggerConfigurations = new HashMap<>();

	@Override
	public void start() throws Exception {

		super.start();

		vertx.eventBus().<JsonObject>consumer(AccessLoggerConstants.EVENTBUS_RAW_EVENT_NAME, event -> {

			JsonObject eventBody = event.body();
			JsonArray identifiers = eventBody.getJsonArray(RawEvent.Request.IDENTIFIERS);
			
			for (Object x : identifiers.getList()) {
				String identifier = (String) x;
				if (resolvedLoggerConfigurations.containsKey(identifier)) {

					List<Object> nativeValues = getNativeValues(resolvedLoggerConfigurations.get(identifier).getResolvedLogElements(), event.body());
					Map<String, Object> internalValues = getInternalValues(eventBody);
					
					for (Appender appender : resolvedLoggerConfigurations.get(identifier).getRawAppender()) {
						appender.push(nativeValues, internalValues);
					}
				}
			}

		});

		vertx.eventBus().<JsonObject>consumer(AccessLoggerConstants.EVENTBUS_REGISTER_EVENT_NAME, event -> 

			event.reply(performRegistration(event.body()))

		);

	}
	
	private Map<String,Object> getInternalValues(final JsonObject eventBody){
		
		return Map.of(AccessLoggerConstants.InternalValues.TIMESTAMP, eventBody.getLong(Data.Type.START_TS_MILLIS.getFieldName()));
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private synchronized JsonObject performRegistration(final JsonObject request) {

		String identifier = request.getString(Registration.Request.IDENTIFIER);
		String logPattern = request.getString(Registration.Request.LOGPATTERN);

		JsonObject response = new JsonObject();

		if (!resolvedLoggerConfigurations.containsKey(identifier)) {

			final ResolvedPatternResult result = patternResolver.resolvePattern(logPattern);

			ResolvedLoggerConfiguration config = new ResolvedLoggerConfiguration();
			config.setOriginalLogPattern(logPattern);
			config.setResolvedLogPattern(result.getResolvedPattern());
			config.setResolvedLogElements(result.getLogElements());

			final Set<Data.Type> requiredTypes = determinateRequiredElementData(result.getLogElements());

			config.setRequiresIncomingHeaders(requiredTypes.contains(Data.Type.REQUEST_HEADERS));
			config.setRequiresOutgoingHeaders(requiredTypes.contains(Data.Type.RESPONSE_HEADERS));
			config.setRequiresCookies(requiredTypes.contains(Data.Type.COOKIES));

			JsonArray appenders = request.getJsonArray(Registration.Request.APPENDERS, new JsonArray());

			appenders.forEach(appender -> {

				JsonObject appenderConfig = (JsonObject) appender;

				JsonObject appenderConstructoreConfig = appenderConfig
						.getJsonObject(Registration.Request.APPENDER_CONFIG, new JsonObject());
				appenderConstructoreConfig.put(AccessLoggerConstants.CONFIG_KEY_RESOLVED_PATTERN,
						config.getResolvedLogPattern());

				final String appenderClassName = appenderConfig.getString(Registration.Request.APPENDER_CLASS_NAME);
				try {
					Class clazz = this.getClass().getClassLoader().loadClass(appenderClassName);
					Appender appenderInstance = (Appender) clazz.getConstructor(JsonObject.class)
							.newInstance(appenderConstructoreConfig);
					config.getRawAppender().add(appenderInstance);

				} catch (Exception ex) {
					throw new AccessLoggerException("Failed to create appender with [" + appenderClassName + "]", ex);
				}

			});

			resolvedLoggerConfigurations.put(identifier, config);

			logger.info("Successfully created config for [" + identifier + "]");
			
			if (config.isRequiresCookies() || config.isRequiresIncomingHeaders()
					|| config.isRequiresOutgoingHeaders()) {
				
				logger.info("Config [" + identifier + "] requires specific data for cookies [" + config.isRequiresCookies() + "], incoming headers [" + config.isRequiresIncomingHeaders() + "], outgoing headers [" + config.isRequiresOutgoingHeaders() + "]");
				
			} else {
				
				logger.info("No specific data required for config [" + identifier + "]");
			
			}

			populateResponse(response, config);

		} else if (resolvedLoggerConfigurations.get(identifier).getOriginalLogPattern().equals(logPattern)) {

			logger.info("Found and reused config for [" + identifier + "]");
			
			populateResponse(response, resolvedLoggerConfigurations.get(identifier));

		} else {
			response.put(Registration.Response.RESULT, Registration.Response.RESULT_FAILED);
		}

		return response;
	}

	private void populateResponse(final JsonObject response, final ResolvedLoggerConfiguration config) {

		response.put(Registration.Response.RESULT, Registration.Response.RESULT_OK);
		response.put(Registration.Response.REQUIRES_COOKIES, config.isRequiresCookies());
		response.put(Registration.Response.REQUIRES_INCOMING_HEADERS, config.isRequiresIncomingHeaders());
		response.put(Registration.Response.REQUIRES_OUTGOING_HEADERS, config.isRequiresOutgoingHeaders());

	}

	
	private List<Object> getNativeValues(final Collection<AccessLogElement> logElements, final JsonObject rawValue) {
		
		List<Object> values = new ArrayList<>(logElements.size());

		for (final AccessLogElement alElement : logElements) {
			values.add(alElement.getNativeValue(rawValue));
		}
		
		return values;

	}

	@Override
	public void stop() throws Exception {

		logger.info("Stopping AccessLoggerProducerVerticle");

		logger.info("Notifying raw appenders about shutdown");
		this.resolvedLoggerConfigurations.values().forEach(resolvedLoggerConfiguration -> 
			resolvedLoggerConfiguration.getRawAppender().forEach(Appender::notifyShutdown)
		);
			
		super.stop();

	}

	Set<Data.Type> determinateRequiredElementData(final Collection<AccessLogElement> logElements) {

		final Set<Data.Type> requiredTypes = new HashSet<>();

		for (final AccessLogElement element : logElements) {
			requiredTypes.addAll(element.claimDataParts());
		}

		return requiredTypes;

	}
}
