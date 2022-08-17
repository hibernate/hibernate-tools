package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BigIntegerTypeTest {

	@Test
	public void testInstance() {
		assertNotNull(BigIntegerType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("big_integer", BigIntegerType.INSTANCE.getName());
	}
	
	@Test
	public void testRegisterUnderJavaType() {
		assertTrue(BigIntegerType.INSTANCE.registerUnderJavaType());
	}

}
