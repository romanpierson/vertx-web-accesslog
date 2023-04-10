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
package com.romanpierson.vertx.web.accesslogger.configuration.pattern;

import java.util.function.Function;
import java.util.function.Supplier;

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;

import io.vertx.core.json.JsonObject;

public class ExtractedPosition {

	private final int start;
	private final int offset;
	private final Supplier<AccessLogElement> elementSupplier;
	private final Function<JsonObject,AccessLogElement> elementFunction;
	private final JsonObject config;
	
	public static ExtractedPosition build(final int start, final int offset, 
			final Supplier<AccessLogElement> elementSupplier) 
	{
		
		return new ExtractedPosition(start, offset, elementSupplier, null, null);
		
	}
	
	public static ExtractedPosition build(final int start, final int offset, 
			final Function<JsonObject,AccessLogElement> elementFunction, JsonObject config) 
	{
		
		return new ExtractedPosition(start, offset, null, elementFunction, config);
		
	}
	
	private ExtractedPosition(final int start, final int offset, 
			final Supplier<AccessLogElement> elementSupplier,
			final Function<JsonObject,AccessLogElement> elementFunction,
			JsonObject config) {
		
		super();
		this.start = start;
		this.offset = offset;
		this.elementSupplier = elementSupplier;
		this.elementFunction = elementFunction;
		this.config = config;
	}

	public int getStart() {
		return start;
	}

	public int getOffset() {
		return offset;
	}

	public Supplier<AccessLogElement> getElementSupplier() {
		return elementSupplier;
	}
	
	public Function<JsonObject,AccessLogElement> getElementFunction(){
		return elementFunction;
	}

	public JsonObject getConfig() {
		return config;
	}
	
	public boolean isCustom() {
		
		if(elementSupplier != null) {
			
			return !elementSupplier.get().getClass().getName().startsWith("com.romanpierson.vertx.web.accesslogger.configuration.element");
			
		}else if(elementFunction != null && config != null) {
			
			return !elementFunction.apply(config).getClass().getName().startsWith("com.romanpierson.vertx.web.accesslogger.configuration.element");
			
		}else {
			throw new IllegalStateException("Extracted Position must either have element supplier or function");
		}
		
	}
		
}
