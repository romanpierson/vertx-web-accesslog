/*
 * Copyright (c) 2016-2024 Roman Pierson
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.romanpierson.vertx.web.accesslogger.AccessLoggerConstants.Request.Data;
import com.romanpierson.vertx.web.accesslogger.configuration.element.impl.RequestElement;
import com.romanpierson.vertx.web.accesslogger.util.VersionUtility;

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

	/**
	@Test
	public void testFindInRawPatternInvalidInputNull(){
	
		assertThrows(IllegalArgumentException.class,
	            ()->{
	            	new RequestElement().findInRawPattern(null);
	            });
		
		
	}
	
	@Test
	public void testFindInRawPatternInvalidInputEmpty(){
	
		assertThrows(IllegalArgumentException.class,
	            ()->{
	            	new RequestElement().findInRawPattern("");
	            });
		
	}
	
	@Test
	public void testFindInRawPatternApacheFirstRequestLine(){
		
		final String expectedOutputWithQuery = "method-value uri-value?query-value " + VersionUtility.getFormattedValue(new JsonObject(valuesWithQuery));
		final String expectedOutputWithoutQuery = "method-value uri-value " + VersionUtility.getFormattedValue(new JsonObject(valuesWithQuery));
		
		ExtractedPosition ep = new RequestElement().findInRawPattern("%r").iterator().next();
		assertNotNull(ep);
		assertEquals(0, ep.getStart());
		assertEquals("%r".length(), ep.getOffset());
		
		// Test if the output of the looked up element is correct
		assertEquals(expectedOutputWithQuery, ep.getElementSupplier().get().getFormattedValue(new JsonObject(valuesWithQuery)));
		assertEquals(expectedOutputWithoutQuery, ep.getElementSupplier().get().getFormattedValue(new JsonObject(valuesWithoutQuery)));
	}
	
	@Test
	public void testFindInRawPatternURI(){
		
		final String expectedOutput = "uri-value";
		
		ExtractedPosition ep1 = new RequestElement().findInRawPattern("%U").iterator().next();
		assertNotNull(ep1);
		assertEquals(0, ep1.getStart());
		assertEquals("%U".length(), ep1.getOffset());
		
		// Test if the output of the looked up element is correct
		assertEquals(expectedOutput, ep1.getElementSupplier().get().getFormattedValue(new JsonObject(valuesWithQuery)));
		
		ExtractedPosition ep2 = new RequestElement().findInRawPattern("cs-uri-stem").iterator().next();
		assertNotNull(ep2);
		assertEquals(0, ep2.getStart());
		assertEquals("cs-uri-stem".length(), ep2.getOffset());
		
		// Test if the output of the looked up element is correct
		assertEquals(expectedOutput, ep2.getElementSupplier().get().getFormattedValue(new JsonObject(valuesWithQuery)));
		
	}
	**/
	
	private Map<String, Object> getAllValues(final String uriValue, final String methodValue, final String queryValue, final String versionValue){
		
		Map<String, Object> values = new HashMap<>();
		
		values.put(Data.Type.URI.getFieldName(), uriValue);
		values.put(Data.Type.VERSION.getFieldName(), versionValue);
		values.put(Data.Type.QUERY.getFieldName(), queryValue);
		values.put(Data.Type.METHOD.getFieldName(), methodValue);
		
		return values;
	}
}
