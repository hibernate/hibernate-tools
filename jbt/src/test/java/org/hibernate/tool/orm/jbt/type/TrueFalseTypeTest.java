package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.type.TrueFalseConverter;
import org.junit.jupiter.api.Test;

public class TrueFalseTypeTest {
	
	@Test
	public void testInstance() {
		assertNotNull(TrueFalseType.INSTANCE);
	}
	
	@Test
	public void testGetName() {
		assertEquals("true_false", TrueFalseType.INSTANCE.getName());
	}
	
	@Test
	public void testGetValueConverter() {
		assertSame(TrueFalseConverter.INSTANCE, TrueFalseType.INSTANCE.getValueConverter());
	}

}
