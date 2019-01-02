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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mdac.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.mdac.vertx.web.accesslogger.configuration.element.impl.RequestElement;
import com.mdac.vertx.web.accesslogger.util.VersionUtility;

import io.vertx.core.json.JsonObject;

/**
 * 
 * Tests {@link RequestElement}
 * 
 * @author Roman Pierson
 *
 */
public class RequestElementTest {
	
	final String httpVersion = "HTTP_1_1";
	final Map<String, Object> valuesWithQuery = getAllValues("uri-value", "method-value", "query-value", httpVersion); 
	final Map<String, Object> valuesWithoutQuery = getAllValues("uri-value", "method-value", null, httpVersion); 

	@Test(expected=IllegalArgumentException.class)
	public void testFindInRawPatternInvalidInputNull(){
	
		new RequestElement().findInRawPattern(null);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFindInRawPatternInvalidInputEmpty(){
	
		new RequestElement().findInRawPattern("");
		
	}
	
	@Test
	public void testFindInRawPatternApacheFirstRequestLine(){
		
		final String expectedOutputWithQuery = "method-value uri-value?query-value " + VersionUtility.getFormattedValue(new JsonObject(valuesWithQuery));
		final String expectedOutputWithoutQuery = "method-value uri-value " + VersionUtility.getFormattedValue(new JsonObject(valuesWithQuery));
		
		ExtractedPosition ep = new RequestElement().findInRawPattern("%r");
		assertNotNull(ep);
		assertEquals(0, ep.getStart());
		assertEquals("%r".length(), ep.getOffset());
		
		// Test if the output of the looked up element is correct
		assertEquals(expectedOutputWithQuery, ep.getElement().getFormattedValue(new JsonObject(valuesWithQuery)));
		assertEquals(expectedOutputWithoutQuery, ep.getElement().getFormattedValue(new JsonObject(valuesWithoutQuery)));
	}
	
	@Test
	public void testFindInRawPatternURI(){
		
		final String expectedOutput = "uri-value";
		
		ExtractedPosition ep1 = new RequestElement().findInRawPattern("%U");
		assertNotNull(ep1);
		assertEquals(0, ep1.getStart());
		assertEquals("%U".length(), ep1.getOffset());
		
		// Test if the output of the looked up element is correct
		assertEquals(expectedOutput, ep1.getElement().getFormattedValue(new JsonObject(valuesWithQuery)));
		
		ExtractedPosition ep2 = new RequestElement().findInRawPattern("cs-uri-stem");
		assertNotNull(ep2);
		assertEquals(0, ep2.getStart());
		assertEquals("cs-uri-stem".length(), ep2.getOffset());
		
		// Test if the output of the looked up element is correct
		assertEquals(expectedOutput, ep2.getElement().getFormattedValue(new JsonObject(valuesWithQuery)));
		
	}
	
	private Map<String, Object> getAllValues(final String uriValue, final String methodValue, final String queryValue, final String versionValue){
		
		Map<String, Object> values = new HashMap<>();
		
		values.put(Data.Type.URI.getFieldName(), uriValue);
		values.put(Data.Type.VERSION.getFieldName(), versionValue);
		values.put(Data.Type.QUERY.getFieldName(), queryValue);
		values.put(Data.Type.METHOD.getFieldName(), methodValue);
		
		return values;
	}
}
