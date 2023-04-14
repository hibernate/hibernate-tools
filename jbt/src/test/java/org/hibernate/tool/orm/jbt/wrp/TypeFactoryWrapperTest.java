package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TypeFactoryWrapperTest {

	@Test
	public void testConstruction() {
		assertNotNull(TypeFactoryWrapper.INSTANCE);
	}

}
