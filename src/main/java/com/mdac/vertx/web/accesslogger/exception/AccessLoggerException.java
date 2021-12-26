/*
 * Copyright (c) 2016-2022 Roman Pierson
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
package com.mdac.vertx.web.accesslogger.exception;

public class AccessLoggerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccessLoggerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessLoggerException(String message) {
		super(message);
	}

}
