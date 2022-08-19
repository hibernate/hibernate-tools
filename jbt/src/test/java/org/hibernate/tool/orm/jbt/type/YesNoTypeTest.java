package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.type.YesNoConverter;
import org.junit.jupiter.api.Test;

public class YesNoTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(YesNoType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("yes_no", YesNoType.INSTANCE.getName());
	}
	
	@Test
	public void testGetValueConverter() {
		assertSame(YesNoConverter.INSTANCE, YesNoType.INSTANCE.getValueConverter());
	}

}
