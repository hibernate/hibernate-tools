package org.hibernate.tools.test.util;

import java.util.Iterator;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.util.MetadataHelper;

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
	
	public static Metadata initializeMetadata(Object test, String[] hbmXmlFiles) {
		String resourcesLocation = '/' + test.getClass().getPackage().getName().replace(".", "/") + '/';
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
		for (int i = 0; i < hbmXmlFiles.length; i++) {
			configuration.addResource(resourcesLocation + hbmXmlFiles[i]);
		}
		return MetadataHelper.getMetadata(configuration);
	}
	 
}
