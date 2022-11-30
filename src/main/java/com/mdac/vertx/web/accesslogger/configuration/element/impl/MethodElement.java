/*
 * Copyright (c) 2016-2023 Roman Pierson
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
package com.mdac.vertx.web.accesslogger.configuration.element.impl;

import java.util.Arrays;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;

public class MethodElement extends GenericAccessLogElement{

	public MethodElement(){
		super(Arrays.asList("cs-method", "%m"), Data.Type.METHOD.getFieldName());
	}
	
}
