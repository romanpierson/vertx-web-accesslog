package com.romanpierson.vertx.web.accesslogger.configuration.element.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class InvalidElement implements AccessLogElement {

	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(String rawPattern) {
		
		Collection<ExtractedPosition> eps = new ArrayList<>();
		eps.add(null);
		
		return eps;
		
	}

	@Override
	public Object getNativeValue(JsonObject values) {
		
		return null;
		
	}

}
