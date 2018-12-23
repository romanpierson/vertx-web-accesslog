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

import java.util.Collection;

import io.vertx.core.json.JsonObject;

public interface Appender {

	/**
	 * 
	 * Push the access events to the appender.
	 * 
	 * @param accessEvents
	 */
	void push(Collection<JsonObject> accessEvents);
	
	default boolean requiresResolvedPattern(){
		
		// Not forcing the implementations to implement if not required
		
		return false;
		
	}
	
	default void setResolvedPattern(String resolvedPattern){
		
		// Not forcing the implementations to implement if not required
		
		return;
	}
	
}
