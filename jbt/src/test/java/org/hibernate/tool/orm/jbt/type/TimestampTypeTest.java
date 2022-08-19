package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TimestampTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(TimestampType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("timestamp", TimestampType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"timestamp",
					"java.sql.Timestamp",
					"java.util.Date"
				},
				TimestampType.INSTANCE.getRegistrationKeys());
	}

}
