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
package com.mdac.vertx.web.accesslogger.verticle;

import java.util.ArrayList;
import java.util.Collection;

import com.mdac.vertx.web.accesslogger.appender.Appender;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;

public class ResolvedLoggerConfiguration {

	public String identifier;
	public String originalLogPattern;
	public String resolvedLogPattern;
	
	public boolean requiresIncomingHeaders;
	public boolean requiresOutgoingHeaders;
	public boolean requiresCookies;
	
	public Collection<AccessLogElement> resolvedLogElements;
	
	public Collection<Appender> rawAppender = new ArrayList<>();
	
}
