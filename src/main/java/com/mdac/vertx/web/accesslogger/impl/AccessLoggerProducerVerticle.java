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

import java.util.Collection;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants;
import com.mdac.vertx.web.accesslogger.appender.Appender;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

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
	
	final private Collection<AccessLogElement> logElements;
	final private Collection<Appender> rawAppenders;
	final private Collection<AbstractVerticle> verticleAppenders;
	final private boolean hasVerticleAppenders;

	public AccessLoggerProducerVerticle(final AccessLoggerOptions accessLoggerOptions, final Collection<AccessLogElement> logElements, final Collection<Appender> rawAppenders, final Collection<AbstractVerticle> verticleAppenders) {
		
		this.logElements = logElements;
		this.rawAppenders = rawAppenders;
		this.verticleAppenders = verticleAppenders;
		this.hasVerticleAppenders = !this.verticleAppenders.isEmpty();
		
	}

	@Override
	public void start() throws Exception {

		super.start();

		LOG.info("Starting AccessLoggerProducerVerticle");
		
		for(AbstractVerticle verticleAppender : this.verticleAppenders) {
			LOG.info("Deploying Appender Verticle of type [{}]", verticleAppender.getClass().getSimpleName());
			this.vertx.deployVerticle(verticleAppender, new DeploymentOptions().setWorker(true));
		}
		
		vertx.eventBus().<JsonObject> consumer(AccessLoggerConstants.EVENTBUS_RAW_EVENT_NAME, event -> {
			
			JsonArray formatted = getFormattedValues(event.body());
			
			if(this.hasVerticleAppenders) {
				vertx.eventBus().publish(AccessLoggerConstants.EVENTBUS_APPENDER_EVENT_NAME, formatted);
			}
			
			for (final Appender appender : rawAppenders) {

				appender.push(formatted);
				
			}
			
		});

		
	}
	
	private JsonArray getFormattedValues(final JsonObject rawValue) {
		
		JsonArray value = new JsonArray();
		
		for(final AccessLogElement alElement : this.logElements){
			final String formattedValue = alElement.getFormattedValue(rawValue);
			value.add(formattedValue != null ? formattedValue : "");
		}
		
		return value;
		
	}

	@Override
	public void stop() throws Exception {

		LOG.info("Stopping AccessLoggerProducerVerticle");
		
		LOG.info("Notifying appenders about shutdown");
		rawAppenders.forEach(appender -> appender.notifyShutdown());
		
		super.stop();

	}

}
