package com.romanpierson.vertx.web.accesslogger.configuration.element.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.romanpierson.vertx.web.accesslogger.configuration.pattern.ExtractedPosition;

public class AccessLogElementTest {

	@Test
	public void testInvalidElement1() {
		
		InvalidElement element = new InvalidElement();
		Collection<ExtractedPosition> eps = element.findInRawPattern("foo");
		
		assertEquals(0, eps.size());
		
	}
	
	@Test
	public void testInvalidElement2() {
		
		InvalidElement2 element = new InvalidElement2();
		Collection<ExtractedPosition> eps = element.findInRawPattern("foo");
		
		assertEquals(0, eps.size());
		
	}
	
}
