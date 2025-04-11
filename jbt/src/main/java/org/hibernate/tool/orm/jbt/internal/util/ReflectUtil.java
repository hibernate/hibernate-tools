/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2022-2025 Red Hat, Inc.
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
