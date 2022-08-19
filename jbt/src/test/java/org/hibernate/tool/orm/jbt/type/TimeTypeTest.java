package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TimeTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(TimeType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("time", TimeType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"time",
					"java.sql.Time"
				},
				TimeType.INSTANCE.getRegistrationKeys());
	}

}
