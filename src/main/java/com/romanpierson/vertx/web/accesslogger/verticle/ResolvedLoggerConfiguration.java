/*
 * Copyright (c) 2016-2025 Roman Pierson
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
package com.romanpierson.vertx.web.accesslogger.verticle;

import java.util.ArrayList;
import java.util.Collection;

import com.romanpierson.vertx.web.accesslogger.appender.Appender;
import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;

public class ResolvedLoggerConfiguration {

	private String identifier;
	private String originalLogPattern;
	private String resolvedLogPattern;
	
	private boolean requiresIncomingHeaders;
	private boolean requiresOutgoingHeaders;
	private boolean requiresCookies;
	
	private Collection<AccessLogElement> resolvedLogElements;
	
	private Collection<Appender> rawAppender = new ArrayList<>();

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getOriginalLogPattern() {
		return originalLogPattern;
	}

	public void setOriginalLogPattern(String originalLogPattern) {
		this.originalLogPattern = originalLogPattern;
	}

	public String getResolvedLogPattern() {
		return resolvedLogPattern;
	}

	public void setResolvedLogPattern(String resolvedLogPattern) {
		this.resolvedLogPattern = resolvedLogPattern;
	}

	public boolean isRequiresIncomingHeaders() {
		return requiresIncomingHeaders;
	}

	public void setRequiresIncomingHeaders(boolean requiresIncomingHeaders) {
		this.requiresIncomingHeaders = requiresIncomingHeaders;
	}

	public boolean isRequiresOutgoingHeaders() {
		return requiresOutgoingHeaders;
	}

	public void setRequiresOutgoingHeaders(boolean requiresOutgoingHeaders) {
		this.requiresOutgoingHeaders = requiresOutgoingHeaders;
	}

	public boolean isRequiresCookies() {
		return requiresCookies;
	}

	public void setRequiresCookies(boolean requiresCookies) {
		this.requiresCookies = requiresCookies;
	}

	public Collection<AccessLogElement> getResolvedLogElements() {
		return resolvedLogElements;
	}

	public void setResolvedLogElements(Collection<AccessLogElement> resolvedLogElements) {
		this.resolvedLogElements = resolvedLogElements;
	}

	public Collection<Appender> getRawAppender() {
		return rawAppender;
	}

	public void setRawAppender(Collection<Appender> rawAppender) {
		this.rawAppender = rawAppender;
	}
	
	
}
