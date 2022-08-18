package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class DateTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(DateType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("date", DateType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"date",
					"java.sql.Date"
				},
				DateType.INSTANCE.getRegistrationKeys());
	}

}
