package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PrimitiveHelperTest {
	
	@Test
	public void testIsPrimitive() {
		assertTrue(PrimitiveHelper.isPrimitive(int.class));
		assertTrue(PrimitiveHelper.isPrimitive(Integer.class));
		assertFalse(PrimitiveHelper.isPrimitive(Object.class));
	}

}
