/*
 * Copyright (c) 2016-2025 Roman Pierson
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
package com.romanpierson.vertx.web.accesslogger.appender.logging.impl;


import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants;
import com.romanpierson.vertx.web.accesslogger.appender.Appender;

import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import static com.romanpierson.vertx.web.accesslogger.util.FormatUtility.getStringifiedParameterValues;

import java.util.List;
import java.util.Map;

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
	public void push(List<Object> rawAccessElementValues, Map<String, Object> internalValues) {

		final String formattedString = String.format(this.resolvedPattern, getStringifiedParameterValues(rawAccessElementValues));

		this.logger.info(formattedString);

	}


}
