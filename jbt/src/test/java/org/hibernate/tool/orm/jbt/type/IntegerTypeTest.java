package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class IntegerTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(IntegerType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("integer", IntegerType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"integer",
					"int",
					"java.lang.Integer"
				},
				IntegerType.INSTANCE.getRegistrationKeys());
	}

}
