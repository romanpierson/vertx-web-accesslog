/*
 * Copyright (c) 2016-2023 Roman Pierson
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
package com.mdac.vertx.web.accesslogger.appender.logging.impl;


import com.mdac.vertx.web.accesslogger.AccessLoggerConstants;
import com.mdac.vertx.web.accesslogger.appender.Appender;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * An implementation of {@link Appender} that writes to standard log
 * 
 * @author Roman Pierson
 *
 */
public class LoggingAppender implements Appender {

	private static final String CONFIG_KEY_LOGGER_NAME = "loggerName";

	private String resolvedPattern;
	private final Logger logger;

	public LoggingAppender(final JsonObject config) {

		if (config.getString(AccessLoggerConstants.CONFIG_KEY_RESOLVED_PATTERN, "").trim().length() == 0) {
			throw new IllegalArgumentException("resolvedPattern must not be empty");
		} else if (config.getString(CONFIG_KEY_LOGGER_NAME, "").trim().length() == 0) {
			throw new IllegalArgumentException("loggerName must not be empty");
		}

		final String loggerName = config.getString(CONFIG_KEY_LOGGER_NAME);

		this.resolvedPattern = config.getString(AccessLoggerConstants.CONFIG_KEY_RESOLVED_PATTERN);
		this.logger = LoggerFactory.getLogger(loggerName);
	}

	@Override
	public void push(JsonArray accessEvent) {

		Object[] parameterValues = getParameterValues(accessEvent);

		final String formattedString = String.format(this.resolvedPattern, parameterValues);

		this.logger.info(formattedString);

	}

	private Object[] getParameterValues(final JsonArray values) {

		final String[] parameterValues = new String[values.size()];

		int i = 0;
		for (final Object xValue : values.getList()) {
			parameterValues[i] = (String) xValue;
			i++;
		}

		return parameterValues;

	}

}
