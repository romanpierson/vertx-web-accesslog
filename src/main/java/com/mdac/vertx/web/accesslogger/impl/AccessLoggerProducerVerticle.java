/*
 * Copyright (c) 2016-2018 Roman Pierson
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

import com.mdac.vertx.web.accesslogger.appender.Appender;

import io.vertx.core.AbstractVerticle;
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

	private BlockingQueue<JsonObject> queue = new LinkedBlockingQueue<>();

	final private Collection<Appender> appenders;
	final private long appenderScheduleInterval;

	public AccessLoggerProducerVerticle(final AccessLoggerOptions accessLoggerOptions, final Collection<Appender> appenders) {
		
		this.appenders = appenders;
		this.appenderScheduleInterval = accessLoggerOptions.getAppenderScheduleInterval();
		
	}

	@Override
	public void start() throws Exception {

		super.start();

		vertx.eventBus().<JsonObject> consumer("accesslogevent", event -> {
			queue.offer(event.body());
		});

		vertx.setPeriodic(this.appenderScheduleInterval, handler -> {

			final int currentSize = this.queue.size();

			if (currentSize > 0) {

				final Collection<JsonObject> drainedValues = new ArrayList<>(currentSize);

				this.queue.drainTo(drainedValues, currentSize);

				for (final Appender appender : appenders) {

					appender.push(drainedValues);
					
				}

			}
		});
	}

	@Override
	public void stop() throws Exception {

		// TODO what happens to pending elements in the queue
		
		super.stop();

	}

}
