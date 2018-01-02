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
package com.mdac.vertx.web.accesslogger;

import java.util.Collection;
import java.util.Collections;

import com.mdac.vertx.web.accesslogger.appender.AppenderOptions;
import com.mdac.vertx.web.accesslogger.appender.logging.impl.LoggingAppender;
import com.mdac.vertx.web.accesslogger.appender.logging.impl.LoggingAppenderOptions;
import com.mdac.vertx.web.accesslogger.impl.AccessLoggerHandlerImpl;
import com.mdac.vertx.web.accesslogger.impl.AccessLoggerOptions;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * A handler that logs access information
 * 
 * @author Roman Pierson
 *
 */
public interface AccessLoggerHandler extends Handler<RoutingContext> {

	/**
	 * 
	 * Creates an access log handler by passing a logging pattern. Internally an instance of {@link LoggingAppender} is used.
	 * 
	 * This is just kept for backward compatibility and will be removed in the next release.
	 * 
	 * @param pattern	The requested logging pattern
	 * @return			The logging handler
	 */
	@Deprecated
	static AccessLoggerHandler create(final String pattern) {
		
	    return create(new AccessLoggerOptions().setPattern(pattern), Collections.singletonList(new LoggingAppenderOptions()));
	    
	}
	
	/**
	 * 
	 * Creates a logging handler by passing an {@link AccessLoggerOptions} configuration and a list of {@link AppenderOptions}
	 * 
	 * @param accessLoggerOptions	The access logger configuration
	 * @param appenderOptions		One or several appender configuration(s)
	 * @return						The logging handler
	 */
	static AccessLoggerHandler create(final AccessLoggerOptions accessLoggerOptions, final Collection<AppenderOptions> appenderOptions){
		
		return new AccessLoggerHandlerImpl(accessLoggerOptions, appenderOptions);
		
	}
	
}
