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
package com.mdac.vertx.web.accesslogger;

import com.mdac.vertx.web.accesslogger.impl.AccessLoggerHandlerImpl;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
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
	 * Creates a logging handler by passing an {@link JsonObject} configuration
	 * 
	 * @param config	The access logger configuration
	 * @return			The logging handler
	 */
	static AccessLoggerHandler create(final JsonObject config){
		
		return new AccessLoggerHandlerImpl(config);
		
	}
	
}
