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

import java.util.Collection;
import java.util.Collections;

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;

/**
 * 
 * Common options to manage the behaviour of the overall access logger
 * 
 * @author Roman Pierson
 *
 */
public class AccessLoggerOptions {

	public static final long DEFAULT_APPENDER_SCHEDULE_INTERVAL = 1000;

	private long appenderScheduleInterval = DEFAULT_APPENDER_SCHEDULE_INTERVAL;

	private String pattern = null;
	private Collection<AccessLogElement> logElements;
	
	/**
	 * Set the interval in ms that should be applied to feed the appender(s)
	 *
	 * @param appenderScheduleInterval
	 *            interval in ms
	 * @return a reference to this, so the API can be used fluently
	 */
	public AccessLoggerOptions setAppenderScheduleInterval(long appenderScheduleInterval) {
		
		if (appenderScheduleInterval < 1) {
			throw new IllegalArgumentException("appenderScheduleInterval must be > 0");
		}
		
		this.appenderScheduleInterval = appenderScheduleInterval;
		
		return this;
		
	}
	
	/**
	 * Set the access log pattern
	 *
	 * @param pattern   the log pattern
	 * @return a reference to this, so the API can be used fluently
	 */
	public AccessLoggerOptions setPattern(String pattern) {
		
		if (pattern == null || pattern.trim().isEmpty() ) {
			throw new IllegalArgumentException("pattern must not be null or empty");
		}
		
		if( getLogElements().size() > 0){
			throw new IllegalArgumentException("must set either pattern or logElements");
		}
		
		this.pattern = pattern;
		
		return this;
		
	}
	
	/**
	 * Set the access log elements
	 *
	 * @param pattern   the log elements
	 * @return a reference to this, so the API can be used fluently
	 */
	public AccessLoggerOptions setLogElements(Collection<AccessLogElement> logElements) {
		
		if (logElements == null || logElements.size() == 0) {
			throw new IllegalArgumentException("logElements must contain at least one item");
		}
		
		if(getPattern() != null){
			throw new IllegalArgumentException("must set either pattern or logElements");
		}
		
		this.logElements = logElements;
		
		return this;
		
	}

	public long getAppenderScheduleInterval() {
		return appenderScheduleInterval;
	}

	public String getPattern() {
		return pattern;
	}

	public Collection<AccessLogElement> getLogElements() {
		return logElements != null ? logElements : Collections.emptyList();
	}
	
}
