package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class FloatTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(FloatType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("float", FloatType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"float",
					"float",
					"java.lang.Float"
				},
				FloatType.INSTANCE.getRegistrationKeys());
	}

}
