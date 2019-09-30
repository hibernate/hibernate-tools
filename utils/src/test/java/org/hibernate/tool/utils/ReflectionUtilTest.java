package org.hibernate.tool.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.util.ReflectionUtil;
import org.junit.jupiter.api.Test;

public class ReflectionUtilTest {
	
	@Test
	public void testClassForName() throws Exception {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		assertNotNull(contextClassLoader);
		try {
			Class<?> clazz = ReflectionUtil.classForName("org.hibernate.tool.util.ReflectionUtil");
			assertSame(ReflectionUtil.class, clazz);
			Thread.currentThread().setContextClassLoader(null);
			clazz = ReflectionUtil.classForName("org.hibernate.tool.util.ReflectionUtil");
			assertSame(ReflectionUtil.class, clazz);
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}

}
