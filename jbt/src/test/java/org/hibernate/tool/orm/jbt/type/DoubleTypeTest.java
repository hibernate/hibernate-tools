package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class DoubleTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(DoubleType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("double", DoubleType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"double",
					"double",
					"java.lang.Double"
				},
				DoubleType.INSTANCE.getRegistrationKeys());
	}

}
