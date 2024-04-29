package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.junit.jupiter.api.Test;

public class TypeRegistryTest {
	
	@Test
	public void testTypeRegistry() {
		Map<String, TypeWrapper> typeRegistry = TypeRegistry.typeRegistry();
		assertNotNull(typeRegistry);
		assertEquals(23, typeRegistry.size());
	}
	
}
