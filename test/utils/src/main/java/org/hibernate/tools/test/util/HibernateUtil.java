package org.hibernate.tools.test.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.metadata.MetadataDescriptor;
import org.hibernate.tool.metadata.MetadataDescriptorFactory;

public class HibernateUtil {
	
	public static class Dialect extends org.hibernate.dialect.Dialect {}
	
	public static ForeignKey getForeignKey(Table table, String fkName) {
		ForeignKey result = null;
		Iterator<?> iter = table.getForeignKeyIterator();
		while (iter.hasNext()) {
			ForeignKey fk = (ForeignKey) iter.next();
			if (fk.getName().equals(fkName)) {
				result = fk;
				break;
			}
		}
		return result;
	}
	
	public static Table getTable(Metadata metadata, String tabName) {
		if (metadata != null) {
			Iterator<Table> iter = metadata.collectTableMappings().iterator();
			while (iter.hasNext()) {
				Table table = (Table) iter.next();
				if (table.getName().equals(tabName)) {
					return table;
				}
			}
		}
		return null;
	}
	
	public static MetadataDescriptor initializeMetadataSources(
			Object test, 
			String[] hbmResourceNames, 
			File hbmFileDir) {
		ResourceUtil.createResources(test, hbmResourceNames, hbmFileDir);
		File[] hbmFiles = new File[hbmResourceNames.length];
		for (int i = 0; i < hbmResourceNames.length; i++) {
			hbmFiles[i] = new File(hbmFileDir, hbmResourceNames[i]);
		}
		Properties properties = new Properties();
		properties.put(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		return MetadataDescriptorFactory.createNativeDescriptor(null, hbmFiles, properties);
	}
	
	public static void addAnnotatedClass(
			MetadataDescriptor metadataSources, 
			Class<?> annotatedClass) {
		try {
			Field metadataSourcesField = metadataSources
					.getClass()
					.getDeclaredField("metadataSources");
			metadataSourcesField.setAccessible(true);
			org.hibernate.boot.MetadataSources bootSources = 
					(org.hibernate.boot.MetadataSources)metadataSourcesField.get(metadataSources);
			bootSources.addAnnotatedClass(annotatedClass);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}
