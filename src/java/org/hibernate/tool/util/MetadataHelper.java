package org.hibernate.tool.util;

import java.lang.reflect.Field;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class MetadataHelper {
	
	public static Metadata getMetadata(Configuration configuration) {
		Metadata result = getMetadataFromField(configuration);
		if (result == null) {
			result = buildFromMetadataSources(configuration);
		}
		return result;
	}
	
	private static Metadata buildFromMetadataSources(Configuration configuration) {
		MetadataSources metadataSources = getMetadataSourcesFromField(configuration);
		StandardServiceRegistryBuilder builder = configuration.getStandardServiceRegistryBuilder();
		builder.applySettings(configuration.getProperties());
		StandardServiceRegistry serviceRegistry = builder.build();
		return metadataSources.buildMetadata(serviceRegistry);
	}
	
	private static MetadataSources getMetadataSourcesFromField(Configuration configuration) {
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

}
