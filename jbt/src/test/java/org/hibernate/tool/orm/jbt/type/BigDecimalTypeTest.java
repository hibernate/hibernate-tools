package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BigDecimalTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(BigDecimalType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("big_decimal", BigDecimalType.INSTANCE.getName());
	}
	
	@Test
	public void testRegisterUnderJavaType() {
		assertTrue(BigDecimalType.INSTANCE.registerUnderJavaType());
	}

}
