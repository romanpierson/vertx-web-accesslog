package com.mdac.vertx.web.accesslogger.impl;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mdac.vertx.web.accesslogger.AccessLoggerHandler;
import com.mdac.vertx.web.accesslogger.configuration.output.OutputConfiguration;
import com.mdac.vertx.web.accesslogger.configuration.pattern.RequestElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.StatusElement;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.Utils;

/**
 * 
 * Access Logger for requests
 * 
 * @author Roman Pierson
 *
 */
@SuppressWarnings("unused")
public class AccessLoggerHandlerImpl implements AccessLoggerHandler {

	private final DateFormat dateTimeFormat = Utils.createRFC1123DateTimeFormatter();

	private Logger logger = LoggerFactory.getLogger(AccessLoggerHandlerImpl.class);
	
	private long timeoutPeriod = 5000L;
	
	private OutputConfiguration outputConfiguration;
	
	public AccessLoggerHandlerImpl(final long timeoutPeriod, final String pattern) {
		
		this.timeoutPeriod = timeoutPeriod;
		
		// For now put a hardcoded OutputConfiguration for testing
		outputConfiguration = new OutputConfiguration("%s \"%s\"", Arrays.asList(new StatusElement(), new RequestElement()), Arrays.asList(logger));
		
	}
	
	
	@Override
	public void handle(final RoutingContext context) {
		
		long timestamp = System.currentTimeMillis();
		
		//System.out.println("Starting TIMEOUT timer with period [" + timeoutPeriod + "]");
		
		// Request Method
		//cs-method
		//%m
		
		/*long tid = context.vertx().setTimer(timeoutPeriod, t -> 
			{
				System.out.println("TIMEOUT Timer with period [" + timeoutPeriod + "] finished after [" + (System.currentTimeMillis() - timestamp) + "]");
				context.fail(408);
			}
		);*/

		//context.addBodyEndHandler(v -> {System.out.println("Cancel TIMEOUT timer "); context.vertx().cancelTimer(tid);});
		
		context.addBodyEndHandler(v -> log(context, timestamp));
		
		//System.out.println("After start timer with id [" + tid + "]");
		
		context.next();
		
	}
	
	private void log(final RoutingContext context, long timestamp){
		
		final HttpServerRequest request = context.request();
		/*
		long time = System.currentTimeMillis() - timestamp;
		
		String userAgent = request.headers().get("user-agent");
        userAgent = userAgent == null ? "-" : userAgent;

        int status = request.response().getStatusCode();
        
        Collection<Object> tokens = null;
        
        String.format("", tokens);
        
        final String message = String.format("%s %s %d %d %s",
          dateTimeFormat.format(new Date(timestamp)),
          request.uri(),
          time,
          status,
          userAgent);
        
        logger.info(message);*/
		final Map<String, Object> values = new HashMap<String, Object>();
		values.put("uri", request.uri());
		values.put("status", request.response().getStatusCode());
		
		outputConfiguration.doLog(values);
		
	}
	
}
