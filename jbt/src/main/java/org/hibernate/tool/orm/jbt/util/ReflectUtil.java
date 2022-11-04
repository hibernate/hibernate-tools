package org.hibernate.tool.orm.jbt.util;

import java.lang.reflect.Constructor;

public class ReflectUtil {
	
	public static Object createInstance(String className) {
		Object result = null;
		Constructor<?> constructor = lookupConstructor(lookupClass(className));
		try {
			result = constructor.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException(
					"Exception while creating new instance of class '" + className + "'", t);
		}
		return result;
	}
	
	private static Class<?> lookupClass(String className) {
		Class<?> clazz = null;
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader != null) {
					clazz = classLoader.loadClass(className);
			} else {
				clazz = Class.forName(className);
			}
		} catch (Throwable t) {
			throw new RuntimeException("Exception while looking up class '" + className + "'", t);
		}
		if (clazz == null) {
			throw new RuntimeException("Class " + className + " cannot be found");
		}
		return clazz;
	}
	
	private static Constructor<?> lookupConstructor(Class<?> clazz) {
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
		} catch (Throwable t) {
			throw new RuntimeException(
					"Exception while looking up constructor for class '" + clazz.getName() + "'", t);
		}
		return constructor;
	}

}
