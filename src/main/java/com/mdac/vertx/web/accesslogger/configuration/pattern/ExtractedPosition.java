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
package com.mdac.vertx.web.accesslogger.configuration.pattern;

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;

public class ExtractedPosition {

	private final int start;
	private final int offset;
	private final AccessLogElement element;
	
	public ExtractedPosition(final int start, final int offset) {
		super();
		this.start = start;
		this.offset = offset;
		this.element = null;
	}
	
	public ExtractedPosition(final int start, final int offset, final AccessLogElement element) {
		super();
		this.start = start;
		this.offset = offset;
		this.element = element;
	}

	public int getStart() {
		return start;
	}

	public int getOffset() {
		return offset;
	}

	public AccessLogElement getElement() {
		return element;
	}
	
	
	
}
