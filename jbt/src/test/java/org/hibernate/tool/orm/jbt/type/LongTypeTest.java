package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class LongTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(LongType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("long", LongType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"long",
					"long",
					"java.lang.Long"
				},
				LongType.INSTANCE.getRegistrationKeys());
	}

}
