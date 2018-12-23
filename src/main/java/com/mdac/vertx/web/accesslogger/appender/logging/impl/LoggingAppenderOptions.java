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
package com.mdac.vertx.web.accesslogger.appender.logging.impl;

import com.mdac.vertx.web.accesslogger.appender.AppenderOptions;
import com.mdac.vertx.web.accesslogger.impl.AccessLoggerHandlerImpl;

public class LoggingAppenderOptions extends AppenderOptions {

	private String loggerName;
	
	public LoggingAppenderOptions(){
		this.setAppenderImplementationClassName(LoggingAppender.class.getName());
	}
	
	
	/**
	 * Sets the logger name to be used when writing the log
	 *
	 * @param loggerName	The logger name
	 * 
	 * @return a reference to this, so the API can be used fluently
	 */
	public LoggingAppenderOptions setLoggerName(final String loggerName) {
		
		if (loggerName == null || loggerName.trim().isEmpty()) {
			throw new IllegalArgumentException("loggerName must not be empty");
		}
		
		this.loggerName = loggerName;
		return this;
	}
	

	public String getLoggerName() {
		return loggerName != null ? loggerName : AccessLoggerHandlerImpl.class.getName();
	}

}
