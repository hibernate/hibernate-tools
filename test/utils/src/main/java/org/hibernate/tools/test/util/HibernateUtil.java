package org.hibernate.tools.test.util;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.metadata.MetadataSourcesFactory;

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
	
	public static Metadata initializeMetadata(
			Object test,
			String[] hbmResourceNames,
			File hbmFileDir) {
		return initializeMetadataSources(test, hbmResourceNames, hbmFileDir)
				.buildMetadata();
	}

	public static org.hibernate.tool.metadata.MetadataSources initializeMetadataSources(
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
		return MetadataSourcesFactory.createNativeSources(null, hbmFiles, properties);
	}
	
	public static Metadata initializeMetadata(Object test, String[] hbmXmlFiles) {
		return initializeMetadataSources(test, hbmXmlFiles).buildMetadata();
	}
	 
	public static MetadataSources initializeMetadataSources(Object test, String[] hbmXmlFiles) {
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataSources result = new MetadataSources(ssrb.build());
		String resourcesLocation = '/' + test.getClass().getPackage().getName().replace(".", "/") + '/';
		for (int i = 0; i < hbmXmlFiles.length; i++) {
			result.addResource(resourcesLocation + hbmXmlFiles[i]);
		}
		return result;
	}
}
