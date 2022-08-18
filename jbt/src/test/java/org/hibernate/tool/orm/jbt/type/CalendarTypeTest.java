package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class CalendarTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(CalendarType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("calendar", CalendarType.INSTANCE.getName());
	}
	
	@Test
	public void testGetRegistrationKeys() {
		assertArrayEquals(
				new String[] {
					"calendar",
					"java.util.Calendar",
					"java.util.GregorianCalendar"
				},
				CalendarType.INSTANCE.getRegistrationKeys());
	}

}
