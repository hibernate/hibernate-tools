package org.hibernate.tool.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.util.ReflectHelper;
import org.junit.jupiter.api.Test;

public class ReflectHelperTest {
	
	@Test
	public void testClassForName() throws Exception {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		assertNotNull(contextClassLoader);
		try {
			Class<?> clazz = ReflectHelper.classForName("org.hibernate.tool.util.ReflectHelper");
			assertSame(ReflectHelper.class, clazz);
			Thread.currentThread().setContextClassLoader(null);
			clazz = ReflectHelper.classForName("org.hibernate.tool.util.ReflectHelper");
			assertSame(ReflectHelper.class, clazz);
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}

}
