package com.mdac.vertx.web.accesslogger.impl;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mdac.vertx.web.accesslogger.AccessLoggerHandler;
import com.mdac.vertx.web.accesslogger.configuration.output.OutputConfiguration;
import com.mdac.vertx.web.accesslogger.configuration.pattern.DurationElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.DurationElement.TimeUnit;
import com.mdac.vertx.web.accesslogger.configuration.pattern.PatternResolver;
import com.mdac.vertx.web.accesslogger.configuration.pattern.RequestElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ResolvedPatternResult;
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
	
	private OutputConfiguration outputConfiguration;
	
	private PatternResolver patternResolver = new PatternResolver();
	
	public AccessLoggerHandlerImpl(final String pattern) {
		
		final ResolvedPatternResult resolvedPattern = patternResolver.resolvePattern(pattern);
		
		if(resolvedPattern != null){
			outputConfiguration = new OutputConfiguration(resolvedPattern.getResolvedPattern(), 
					resolvedPattern.getLogElements(), 
					Arrays.asList(logger));
		}
		
	}
	
	
	@Override
	public void handle(final RoutingContext context) {
		
		long startTSmillis = System.currentTimeMillis();
		
		context.addBodyEndHandler(v -> log(context, startTSmillis));
		
		context.next();
		
	}
	
	private void log(final RoutingContext context, long startTSmillis){
		
		final HttpServerRequest request = context.request();
		
		final Map<String, Object> values = new HashMap<String, Object>();
		values.put("uri", request.uri());
		values.put("method", request.method());
		values.put("status", request.response().getStatusCode());
		values.put("startTSmillis", startTSmillis);
		values.put("endTSmillis", System.currentTimeMillis());
		values.put("version", request.version());
		
		outputConfiguration.doLog(values);
		
	}
	
}
