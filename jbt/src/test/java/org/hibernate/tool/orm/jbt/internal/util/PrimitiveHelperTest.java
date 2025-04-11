/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PrimitiveHelperTest {
	
	@Test
	public void testIsPrimitiveWrapperClass() {
		assertFalse(PrimitiveHelper.isPrimitiveWrapperClass(int.class));
		assertTrue(PrimitiveHelper.isPrimitiveWrapperClass(Integer.class));
		assertFalse(PrimitiveHelper.isPrimitiveWrapperClass(Object.class));
	}
	
	@Test
	public void testIsPrimitive() {
		assertTrue(PrimitiveHelper.isPrimitive(int.class));
		assertTrue(PrimitiveHelper.isPrimitive(Integer.class));
		assertFalse(PrimitiveHelper.isPrimitive(Object.class));
	}
	
	@Test
	public void testGetPrimitiveClass() {
		assertNull(PrimitiveHelper.getPrimitiveClass(Object.class));
		assertSame(int.class, PrimitiveHelper.getPrimitiveClass(Integer.class));
	}

}
