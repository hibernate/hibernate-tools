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
