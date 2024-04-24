package org.hibernate.tool.orm.jbt.internal.util;

import java.util.Map;

public class PrimitiveHelper {

	private static final Map<Class<?>, Class<?>> PRIMITIVE_CLASSES_MAP = Map.of(
			Integer.class, int.class,
			Short.class, short.class,
			Long.class, long.class,
			Double.class, double.class,
			Float.class, float.class,
			Character.class, char.class,
			Byte.class, byte.class,
			Boolean.class, boolean.class);
	
	public static boolean isPrimitiveWrapperClass(Class<?> candidateClass) {
		return PRIMITIVE_CLASSES_MAP.keySet().contains(candidateClass);
	}
		
	private static boolean isPrimitiveClass(Class<?> candidateClass) {
		return PRIMITIVE_CLASSES_MAP.values().contains(candidateClass);
	}
	
	public static boolean isPrimitive(Class<?> candidateClass) {
		return isPrimitiveWrapperClass(candidateClass) || isPrimitiveClass(candidateClass);
	}

}
