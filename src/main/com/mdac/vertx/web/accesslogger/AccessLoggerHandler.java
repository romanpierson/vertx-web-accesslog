package com.mdac.vertx.web.accesslogger;

import com.mdac.vertx.web.accesslogger.impl.AccessLoggerHandlerImpl;

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

	static AccessLoggerHandler create(final long timeoutPeriod) {
	    return new AccessLoggerHandlerImpl(timeoutPeriod, "");
	}
	
}
