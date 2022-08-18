package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class BooleanTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(BooleanType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("boolean", BooleanType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"boolean",
					"boolean",
					"java.lang.Boolean"
				},
				BooleanType.INSTANCE.getRegistrationKeys());
	}

}
