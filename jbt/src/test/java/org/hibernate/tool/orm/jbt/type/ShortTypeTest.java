package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ShortTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(ShortType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("short", ShortType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"short",
					"short",
					"java.lang.Short"
				},
				ShortType.INSTANCE.getRegistrationKeys());
	}

}
