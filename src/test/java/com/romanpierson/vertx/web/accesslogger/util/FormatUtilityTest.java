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
package com.romanpierson.vertx.web.accesslogger.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

/**
 * 
 * Tests {@link FormatUtility}
 * 
 * @author Roman Pierson
 *
 */
public class FormatUtilityTest {

	@Test
	public void test() {
		
		JsonObject values = new JsonObject()
				.put("myInteger", Integer.valueOf(1))
				.put("myString", "foo");
		
		assertEquals("1", FormatUtility.getIntegerOrNull(values, "myInteger"));
		assertNull(FormatUtility.getIntegerOrNull(values, "myIntegerThatDoesNotExists"));
		assertEquals("foo", FormatUtility.getStringOrNull(values, "myString"));
		assertNull(FormatUtility.getStringOrNull(values, "myStringThatDoesNotExists"));
		
	}
}
