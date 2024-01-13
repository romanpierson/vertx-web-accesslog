/*
 * Copyright (c) 2016-2024 Roman Pierson
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
package com.romanpierson.vertx.web.accesslogger.appender;

import java.util.List;
import java.util.Map;

/**
 * 
 * An IF defining an appender that can handle Access Events
 * 
 * @author Roman Pierson
 *
 */
public interface Appender {

	/**
	 * 
	 * Push the access element values to the appender.
	 * 
	 * Those values can contain native data types and null values
	 * 
	 * Its the appenders responsibility to implement local storage 
	 * 
	 * @param rawAccessElementValues	List of access events the appender should handle - those are no copies
	 * @param internalValues			A list of internal values that are sent independent from the configured access log elements
	 */
	void push(List<Object> rawAccessElementValues, Map<String, Object> internalValues);
	
	
	/**
	 * Is called by the AccessLogger when the application is shutdown and gives the appender the chance to perform additional actions eg in case data is buffered etc
	 */
	default void notifyShutdown() {
		
		// Not forcing the implementations to implement if not required
		
	}
	
	
}
