package com.mdac.vertx.web.accesslogger.configuration.pattern;

import java.util.Collection;

public class ResolvedPatternResult {

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
