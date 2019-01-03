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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants;
import com.mdac.vertx.web.accesslogger.appender.Appender;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;

import io.vertx.core.AbstractVerticle;
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
	
	private BlockingQueue<JsonObject> queue = new LinkedBlockingQueue<>();

	final private Collection<AccessLogElement> logElements;
	final private Collection<Appender> appenders;
	final private long appenderScheduleInterval;

	public AccessLoggerProducerVerticle(final AccessLoggerOptions accessLoggerOptions, final Collection<AccessLogElement> logElements, final Collection<Appender> appenders) {
		
		this.logElements = logElements;
		this.appenders = appenders;
		this.appenderScheduleInterval = accessLoggerOptions.getAppenderScheduleInterval();
		
	}

	@Override
	public void start() throws Exception {

		super.start();

		vertx.eventBus().<JsonObject> consumer(AccessLoggerConstants.EVENTBUS_EVENT_NAME, event -> {
			queue.offer(event.body());
		});

		vertx.setPeriodic(this.appenderScheduleInterval, handler -> {

			if (!this.queue.isEmpty()) {

				pushDataToAppenders();

			}
		});
	}

	private void pushDataToAppenders() {
		
		final int currentSize = this.queue.size();
		
		final Collection<JsonObject> drainedRawValues = new ArrayList<>(currentSize);

		this.queue.drainTo(drainedRawValues, currentSize);

		final Collection<JsonArray> values = new ArrayList<>(drainedRawValues.size());
		
		for(JsonObject drainedRawValue : drainedRawValues) {
			
			JsonArray value = new JsonArray();
			
			for(final AccessLogElement alElement : this.logElements){
				final String formattedValue = alElement.getFormattedValue(drainedRawValue);
				value.add(formattedValue != null ? formattedValue : "");
			}
			
			values.add(value);
		}
		
		for (final Appender appender : appenders) {

			appender.push(values);
			
		}
	}

	@Override
	public void stop() throws Exception {

		LOG.info("Stopping producer verticle");
		
		// Assuming the timer got already cancelled by vertx when stopping the verticle
		
		if(queue.isEmpty()) {
			
			LOG.info("No pending events left in queue - no action needed");
			
		} else {
			
			pushDataToAppenders();
			
			LOG.info("Notifying appenders about shutdown");
			appenders.forEach(appender -> appender.notifyShutdown());
		}
		
		super.stop();

	}

}
