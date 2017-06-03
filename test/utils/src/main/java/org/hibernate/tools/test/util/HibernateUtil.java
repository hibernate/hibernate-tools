package org.hibernate.tools.test.util;

import java.util.Iterator;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

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
	
	public static Configuration initializeConfiguration(Object test, String[] hbmXmlFiles) {
		String resourcesLocation = '/' + test.getClass().getPackage().getName().replace(".", "/") + '/';
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.loadProperties(resourcesLocation + "hibernate.properties");
		MetadataSources metadataSources = new MetadataSources(ssrb.build());
		for (int i = 0; i < hbmXmlFiles.length; i++) {
			metadataSources.addResource(resourcesLocation + hbmXmlFiles[i]);
		}
		Configuration configuration = new Configuration(metadataSources);
		configuration.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
		metadataSources.buildMetadata();
		return configuration;
	}
	 
}
