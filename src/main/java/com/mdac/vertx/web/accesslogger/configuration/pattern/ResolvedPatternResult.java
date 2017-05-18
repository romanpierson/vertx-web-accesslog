/*
 * Copyright (c) 2016-2017 Roman Pierson
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
