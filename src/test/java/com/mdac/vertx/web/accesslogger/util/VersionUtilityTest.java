/*
 * Copyright (c) 2016-2017 Roman Pierson
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
package com.mdac.vertx.web.accesslogger.util;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import io.vertx.core.http.HttpVersion;

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
		
		final String versionNone = "-";
		final String version10 = "HTTP/1.0";
		final String version11 = "HTTP/1.1";
		final String version20 = "HTTP/2.0";
		
		assertEquals(version10, VersionUtility.getFormattedValue(Collections.singletonMap("version", "HTTP_1_0")));
		assertEquals(version11, VersionUtility.getFormattedValue(Collections.singletonMap("version", "HTTP_1_1")));
		assertEquals(version20, VersionUtility.getFormattedValue(Collections.singletonMap("version", "HTTP_2")));
		assertEquals(versionNone, VersionUtility.getFormattedValue(Collections.singletonMap("version", "foo")));
		assertEquals(versionNone, VersionUtility.getFormattedValue(Collections.singletonMap("version", "")));
		assertEquals(versionNone, VersionUtility.getFormattedValue(Collections.singletonMap("version", null)));
		assertEquals(versionNone, VersionUtility.getFormattedValue( null));
		
	}
}
