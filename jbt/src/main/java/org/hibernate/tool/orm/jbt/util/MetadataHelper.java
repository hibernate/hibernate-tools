package org.hibernate.tool.orm.jbt.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class MetadataHelper {
	
	public static Metadata getMetadata(Configuration configuration) {
		Metadata result = getMetadataFromMethod(configuration);
		if (result == null) {
			result = getMetadataFromField(configuration);
		}
		if (result == null) {
			result = buildFromMetadataSources(configuration);
		}
		return result;
	}
	
	public static MetadataSources getMetadataSources(Configuration configuration) {
		MetadataSources result = null;
		Field metadataSourcesField = getField("metadataSources", configuration);
		if (metadataSourcesField != null) {
			try {
				metadataSourcesField.setAccessible(true);
				result = 
						(MetadataSources)metadataSourcesField.get(configuration);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		if (result == null) {
			result = new MetadataSources();
		}
		return result;
	}
	
	private static Metadata getMetadataFromMethod(Configuration configuration) {
		Metadata result = null;
		Method metadataMethod = getMethod("getMetadata", configuration);
		if (metadataMethod != null) {
			try {
				metadataMethod.setAccessible(true);
				result = (Metadata)metadataMethod.invoke(
						configuration, 
						new Object[] {});
			} catch (InvocationTargetException | 
					IllegalAccessException | 
					IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	private static Method getMethod(String methodName, Object target) {
		Method result = null;
		Class<?> clazz = target.getClass();
		try {
			result = clazz.getMethod(methodName, new Class[] {});
		} catch (NoSuchMethodException | SecurityException e1) {
			// ignore;
		}
		return result;
	}

	private static Metadata getMetadataFromField(Configuration configuration) {
		Metadata result = null;
		Field metadataField = getField("metadata", configuration);
		if (metadataField != null) {
			try {
				metadataField.setAccessible(true);
				result = (Metadata)metadataField.get(configuration);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	private static Field getField(String fieldName, Object target) {
		Field result = null;
		Class<?> clazz = target.getClass();
		while (clazz != null) {
			try {
				result = clazz.getDeclaredField(fieldName);
				// if it exists, exit the while loop
				break;
			} catch (NoSuchFieldException e) {
				// if it doesn't exist, look in the superclass
				clazz = clazz.getSuperclass();
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	private static Metadata buildFromMetadataSources(Configuration configuration) {
		MetadataSources metadataSources = getMetadataSources(configuration);
		StandardServiceRegistryBuilder builder = configuration.getStandardServiceRegistryBuilder();
		builder.applySettings(configuration.getProperties());
		StandardServiceRegistry serviceRegistry = builder.build();
		return metadataSources.buildMetadata(serviceRegistry);
	}
	
}
