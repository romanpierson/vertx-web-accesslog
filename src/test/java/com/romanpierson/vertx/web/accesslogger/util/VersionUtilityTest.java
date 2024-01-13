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
package com.romanpierson.vertx.web.accesslogger.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

/**
 * 
 * Tests {@link VersionUtility}
 * 
 * @author Roman Pierson
 *
 */
public class VersionUtilityTest {

	@Test
	public void testExtractsCorrectVersion() {
		
		final String version10 = "HTTP/1.0";
		final String version11 = "HTTP/1.1";
		final String version20 = "HTTP/2.0";
		
		
		assertEquals(version10, VersionUtility.getFormattedValue(new JsonObject().put("version", "HTTP_1_0")));
		assertEquals(version11, VersionUtility.getFormattedValue(new JsonObject().put("version", "HTTP_1_1")));
		assertEquals(version20, VersionUtility.getFormattedValue(new JsonObject().put("version", "HTTP_2")));
		assertEquals("foo", VersionUtility.getFormattedValue(new JsonObject().put("version",  "foo")));
		assertNull(VersionUtility.getFormattedValue(new JsonObject().put("version", "")));
		assertNull(VersionUtility.getFormattedValue(new JsonObject().putNull("version")));
		
	}
}
