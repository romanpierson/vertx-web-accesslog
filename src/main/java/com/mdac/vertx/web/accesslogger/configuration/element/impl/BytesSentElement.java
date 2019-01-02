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
package com.mdac.vertx.web.accesslogger.configuration.element.impl;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class BytesSentElement implements AccessLogElement{

	private final Mode mode;
	
	private enum Mode{
		
		NO_BYTES_NULL,	
		NO_BYTES_DASH
		
	}
	
	public BytesSentElement(){
		
		this.mode = null;
		
	}
	
	private BytesSentElement(final Mode mode){
		
		this.mode = mode;
		
	}
	
	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		String patternDash = "%b";
		
		int index = rawPattern.indexOf(patternDash);
		
		if(index >= 0){
			
				return new ExtractedPosition(index, patternDash.length(), new BytesSentElement(Mode.NO_BYTES_DASH));
			
		}
		
		String patternNull = "%B";
		
		index = rawPattern.indexOf(patternNull);
		
		if(index >= 0){
			
				return new ExtractedPosition(index, patternNull.length(), new BytesSentElement(Mode.NO_BYTES_NULL));
			
		}
		
		return null;
	}
	
	@Override
	public String getFormattedValue(final JsonObject values) {
	
		if(values.containsKey(Data.Type.BYTES_SENT.getFieldName())){
			
			return "" + values.getLong(Data.Type.BYTES_SENT.getFieldName());
			
		} else {
			
			return Mode.NO_BYTES_DASH.equals(this.mode) ? "-" : "0";
			
		}
		
	}
	
}
