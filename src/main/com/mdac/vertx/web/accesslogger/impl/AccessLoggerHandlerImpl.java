package com.mdac.vertx.web.accesslogger.impl;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

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
public class AccessLoggerHandlerImpl implements com.mdac.vertx.web.accesslogger.AccessLoggerHandler {

	private final DateFormat dateTimeFormat = Utils.createRFC1123DateTimeFormatter();

	private Logger logger = LoggerFactory.getLogger(AccessLoggerHandlerImpl.class);
	
	
	private long timeoutPeriod = 5000L;
	
	public AccessLoggerHandlerImpl(final long timeoutPeriod, final String pattern) {
		this.timeoutPeriod = timeoutPeriod;
	}
	
	
	@Override
	public void handle(final RoutingContext context) {
		
		long timestamp = System.currentTimeMillis();
		
		//System.out.println("Starting TIMEOUT timer with period [" + timeoutPeriod + "]");
		
		
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
        
        System.out.println(message);
        
        logger.info(message);
		
	}
	
	char elementIdentifier = '%';
	char [] allowedElementIdentifiers = new char[]{'h','l','u','t','r','s','b','i'};
	
	
	protected boolean isElementIdentifier(final char identifier){
		
		for(final char c : allowedElementIdentifiers){
			if(identifier == c){
				return true;
			}
		}
		
		return false;
	}
	
	public ResolvedPatternResult resolvePattern(final String pattern){
		
		// %h %l %u %t "%r" %s %b "%{Referer}i" "%{User-Agent}i"
		
		// First split by potential "
		pattern.split("\"");
		
		
		
		boolean inElement = false;      // If we are in a element (defined by %
		boolean inElementSpecificator = false; // If we are in a element specificator identified by {ﬁ
		StringBuilder sbSpecificator = null;
		String currentElement = null;
		
		for(final byte b : pattern.getBytes()){
			
			final char ch = (char) b;
				
			if(inElement){
				
				if(isElementIdentifier(ch)){
					currentElement = "" + ch;
				} else if (ch == '{'){
					inElementSpecificator = true;
					sbSpecificator = new StringBuilder();
				} else {
					// This should basically mean that the element has finished
					
				}
				
				
			} else if (elementIdentifier == ch) {
				
				if(inElement){
					
					if(currentElement == null){
						// We previously were reading an element but we could not get its identifier - invalid
						throw new RuntimeException("Invalid pattern");
					} else {
						
					}
					
					// Previous element needs to be terminated
					
					System.out.println(currentElement);
					
					
					currentElement = null;
					
				}
				
				inElement = true;
			}
			
			System.out.println((char) b);
		}
		
		
		return null;
		
	}
	
	public static void main(String[] args) {
		
		new AccessLoggerHandlerImpl(5000, "").resolvePattern("%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\"");
		
	}
	
	private class ResolvedPatternResult{
		
		private final String resolvedPattern;
		private final Collection<AccessLogElement> logElements;
		
		public ResolvedPatternResult(String resolvedPattern, Collection<AccessLogElement> logElements) {
			super();
			this.resolvedPattern = resolvedPattern;
			this.logElements = logElements;
		}

		public String getResolvedPattern() {
			return resolvedPattern;
		}

		public Collection<AccessLogElement> getLogElements() {
			return logElements;
		}
		
		
		
	}
	
}
