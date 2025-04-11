/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	public static Class<?> getPrimitiveClass(Class<?> candidateClass) {
		return PRIMITIVE_CLASSES_MAP.get(candidateClass);
	}

}
