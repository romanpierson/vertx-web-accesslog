package com.romanpierson.vertx.web.accesslogger.configuration.element.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.romanpierson.vertx.web.accesslogger.configuration.element.AccessLogElement;
import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

import io.vertx.core.json.JsonObject;

public class InvalidElement2 implements AccessLogElement {

	@Override
	public Collection<ExtractedPosition> findInRawPatternInternal(String rawPattern) {
		
		Collection<ExtractedPosition> eps = new ArrayList<>();
		
		ExtractedPosition ep = ExtractedPosition.build(-1, 0, InvalidElement2::new);
		
		eps.add(ep);
		
		return eps;
		
	}

	@Override
	public Object getNativeValue(JsonObject values) {
		
		return null;
		
	}

}
