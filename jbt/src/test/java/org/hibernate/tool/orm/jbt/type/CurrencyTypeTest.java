package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CurrencyTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(CurrencyType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("currency", CurrencyType.INSTANCE.getName());
	}
	
	@Test
	public void testRegisterUnderJavaType() {
		assertTrue(CurrencyType.INSTANCE.registerUnderJavaType());
	}

}
