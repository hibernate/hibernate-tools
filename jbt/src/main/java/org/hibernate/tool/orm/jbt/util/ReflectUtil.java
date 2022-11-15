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
	
	public static Object createInstance(
			String className, 
			Class<?>[] parameterTypes, 
			Object[] parameters) {
		Object result = null;
		Constructor<?> constructor = lookupConstructor(lookupClass(className), parameterTypes);
		try {
			result = constructor.newInstance(parameters);
		} catch (Throwable t) {
			throw new RuntimeException(
					"Exception while creating new instance of class '" + className + "'", t);
		}
		return result;
	}
	
	public static Class<?> lookupClass(String className) {
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
	
	public static Constructor<?> lookupConstructor(Class<?> clazz, Class<?>...parameterTypes ) {
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getConstructor(parameterTypes);
		} catch (Throwable t) {
			throw new RuntimeException(
					"Exception while looking up constructor for class '" + clazz.getName() + "'", t);
		}
		return constructor;
	}

}
