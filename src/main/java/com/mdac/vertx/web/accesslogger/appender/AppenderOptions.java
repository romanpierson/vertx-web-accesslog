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
package com.mdac.vertx.web.accesslogger.appender;

/**
 * 
 * Common options to manage the behaviour of an appender
 * 
 * @author Roman Pierson
 *
 */
public class AppenderOptions {

	private String appenderImplementationClassName;
	
	/**
	 * Creates an {@link AppenderOptions} instance 
	 *
	 * @param appenderImplementationClassName
	 * 
	 * @return a reference to this, so the API can be used fluently
	 */
	public AppenderOptions setAppenderImplementationClassName(final String appenderImplementationClassName) {
		
		if (appenderImplementationClassName == null || appenderImplementationClassName.trim().isEmpty() ) {
			throw new IllegalArgumentException("appenderImplementationClassName must not be null or empty");
		}
		
		this.appenderImplementationClassName = appenderImplementationClassName;
		
		return this;
	}

	public String getAppenderImplementationClassName() {
		
		return appenderImplementationClassName;
		
	}
	
	
	
	
}
