package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.mdac.vertx.web.accesslogger.util.VersionUtility;

public class RequestElement implements AccessLogElement{

	private final RequestLogMode requestLogMode;
	
	private enum RequestLogMode{
		
		APACHE_FIRST_REQUEST_LINE,	// Identical to %m %U%q %H" will log the method, path, query-string, and protocol
		URI,
		QUERY_STRING,
		URI_QUERY
		
	}
	
	public RequestElement() {
		this.requestLogMode = null;
	}
	
	private RequestElement(final RequestLogMode requestLogMode) {
		this.requestLogMode = requestLogMode;
	}

	@Override
	public ExtractedPosition findInRawPattern(final String rawPattern, final int start) {
		
		// Apache request format
		final String requestPattern = "%r";
		int index = rawPattern.indexOf(requestPattern);
			
		if(index >= 0){
				
			if(start == -1 || index <= start)
			{
				return new ExtractedPosition(index, requestPattern.length(), new RequestElement(RequestLogMode.APACHE_FIRST_REQUEST_LINE));
			}
		}
		
		// URI mode only
		final Collection<String> urlPatterns = Arrays.asList("%U", "cs-uri-stem");
		
		for (final String urlPattern : urlPatterns){
			index = rawPattern.indexOf(urlPattern);
			
			if(index >= 0){
					
				if(start == -1 || index <= start)
				{
					return new ExtractedPosition(index, urlPattern.length(), new RequestElement(RequestLogMode.URI));
				}
			}
		}
		
		// URI query mode only
		final Collection<String> queryOnlyPatterns = Arrays.asList("%q", "cs-uri-query");
				
		for (final String queryOnlyPattern : queryOnlyPatterns){
			index = rawPattern.indexOf(queryOnlyPattern);
					
			if(index >= 0){
							
				if(start == -1 || index <= start)
				{
					return new ExtractedPosition(index, queryOnlyPattern.length(), new RequestElement(RequestLogMode.QUERY_STRING));
				}
			}
		}
		
		// Complete URI including query
		final String uriQueryPattern = "cs-uri";
		index = rawPattern.indexOf(uriQueryPattern);
			
		if(index >= 0){
				
			if(start == -1 || index <= start)
			{
				return new ExtractedPosition(index, uriQueryPattern.length(), new RequestElement(RequestLogMode.URI_QUERY));
			}
		}
		
		return null;
	}

	@Override
	public String getFormattedValue(final Map<String, Object> values) {
		
		final StringBuilder sb = new StringBuilder();
		
		if(RequestLogMode.APACHE_FIRST_REQUEST_LINE.equals(this.requestLogMode)){
			
			sb.append("\"");
			
			sb.append(values.get("method")).append(' ');
			
		}
		
		if(!RequestLogMode.QUERY_STRING.equals(this.requestLogMode)){
			sb.append(values.get("uri"));
		}
		
		if(!RequestLogMode.URI.equals(this.requestLogMode)
			&& values.containsKey("query")){
			sb.append('?').append(values.get("query"));
		}
		
		
		if(RequestLogMode.APACHE_FIRST_REQUEST_LINE.equals(this.requestLogMode)){
			
			sb.append(' ').append(VersionUtility.getFormattedValue(values));
			
			sb.append("\"");
		}
		
		return sb.toString();
		
	}

}
