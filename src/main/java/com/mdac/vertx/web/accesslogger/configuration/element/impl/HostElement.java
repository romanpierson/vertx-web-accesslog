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

import com.mdac.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.mdac.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class HostElement implements AccessLogElement{

	private final Mode mode;
	
	private enum Mode{
		
		REMOTE_HOST,	
		LOCAL_HOST,
		LOCAL_PORT
		
	}
	
	public HostElement(){
		
		this.mode = null;
		
	}
	
	public HostElement(final Mode mode){
		
		if(mode == null){
			throw new IllegalArgumentException("mode must not be  null");
		}
		
		this.mode = mode;
		
	}
	
	@Override
	public ExtractedPosition findInRawPatternInternal(final String rawPattern) {
		
		ExtractedPosition foundPosition = null;
		
		String patternRemoteHost = "%h";
		
		int index = rawPattern.indexOf(patternRemoteHost);
		
		if(index >= 0){
			
				foundPosition = new ExtractedPosition(index, patternRemoteHost.length(), new HostElement(Mode.REMOTE_HOST));
			
		}
		
		String patternLocalHost = "%v";
		
		index = rawPattern.indexOf(patternLocalHost);
		
		if(index >= 0){
			
			
				if(foundPosition == null || index < foundPosition.getStart()){
					foundPosition = new ExtractedPosition(index, patternLocalHost.length(), new HostElement(Mode.LOCAL_HOST));
				}
			
		}
		
		String patternLocalPort = "%p";
		
		index = rawPattern.indexOf(patternLocalPort);
		
		if(index >= 0){
			
			
				if(foundPosition == null || index < foundPosition.getStart()){
					foundPosition = new ExtractedPosition(index, patternLocalPort.length(), new HostElement(Mode.LOCAL_PORT));
				}
			
		}
		
		return foundPosition;
	}
	
	@Override
	public String getFormattedValue(final JsonObject values) {
	
		switch (this.mode){
		
			case LOCAL_HOST:
				return "" + values.getString("localHost");
			case LOCAL_PORT:
				return "" + values.getInteger("localPort");
			case REMOTE_HOST:
				return "" + values.getString("remoteHost");
			default:
				break;
			
		}
		
		return "-";
	}
	
}
