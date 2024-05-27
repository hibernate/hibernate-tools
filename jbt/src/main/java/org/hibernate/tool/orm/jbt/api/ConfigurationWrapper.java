package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public interface ConfigurationWrapper extends Wrapper {

	String getProperty(String property);
	ConfigurationWrapper addFile(File file);
	void setProperty(String name, String value);
	ConfigurationWrapper setProperties(Properties properties);
	void setEntityResolver(EntityResolver entityResolver);
	void setNamingStrategy(NamingStrategy namingStrategy);
	Properties getProperties();
	void addProperties(Properties properties);
	ConfigurationWrapper configure(Document document);
	ConfigurationWrapper configure(File file);
	ConfigurationWrapper configure();
	void addClass(Class<?> clazz);
	void buildMappings();
	SessionFactory buildSessionFactory();
	Iterator<PersistentClass> getClassMappings();
	void setPreferBasicCompositeIds(boolean b);
	void setReverseEngineeringStrategy(RevengStrategy strategy);
	void readFromJDBC();
	PersistentClass getClassMapping(String string);
	NamingStrategy getNamingStrategy();
	EntityResolver getEntityResolver();
	Iterator<Table> getTableMappings();
	
}
