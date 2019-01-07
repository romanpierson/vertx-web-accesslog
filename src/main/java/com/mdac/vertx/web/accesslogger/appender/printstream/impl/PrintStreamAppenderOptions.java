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
package com.mdac.vertx.web.accesslogger.appender.printstream.impl;

import java.io.PrintStream;

import com.mdac.vertx.web.accesslogger.appender.AppenderOptions;

public class PrintStreamAppenderOptions extends AppenderOptions {

	private PrintStream printStream;
	
	public PrintStreamAppenderOptions(){
		this.setAppenderImplementationClassName(PrintStreamAppender.class.getName());
	}
	
	/**
	 * Creates an {@link PrintStreamAppenderOptions} instance
	 *
	 * @param printStream
	 * 
	 * @return a reference to this, so the API can be used fluently
	 */
	public PrintStreamAppenderOptions setPrintStream(final PrintStream printStream) {
		
		if (printStream == null ) {
			throw new IllegalArgumentException("printStream must not be null");
		}
		
		this.printStream = printStream;
		
		return this;
		
	}
	
	public PrintStream getPrintStream() {
		
		return printStream;
		
	}

	@Override
	public boolean requiresResolvedPattern() {
		
		return true;
		
	}
	
	

}
