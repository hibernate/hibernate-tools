package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TypeFactoryTest {
	
	@Test
	public void testConstruction() {
		assertNotNull(TypeFactory.INSTANCE);
	}

}
