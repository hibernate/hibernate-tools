package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ByteTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(ByteType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("byte", ByteType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"byte",
					"byte",
					"java.lang.Byte"
				},
				ByteType.INSTANCE.getRegistrationKeys());
	}

}
