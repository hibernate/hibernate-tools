package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.junit.jupiter.api.Test;

public class TypeFactoryWrapperHelperTest {
	
	@Test
	public void testTypeRegistry() {
		Map<String, TypeWrapper> typeRegistry = TypeFactoryWrapperHelper.typeRegistry();
		assertNotNull(typeRegistry);
		assertEquals(23, typeRegistry.size());
	}

}
