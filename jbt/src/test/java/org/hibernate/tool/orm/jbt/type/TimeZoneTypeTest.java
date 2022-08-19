package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TimeZoneTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(TimeZoneType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("timezone", TimeZoneType.INSTANCE.getName());
	}
	
	@Test
	public void testRegisterUnderJavaType() {
		assertTrue(TimeZoneType.INSTANCE.registerUnderJavaType());
	}

}
