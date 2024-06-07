package org.hibernate.tool.orm.jbt.api.wrp;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public interface ConfigurationWrapper extends Wrapper {

	String getProperty(String property);
	ConfigurationWrapper addFile(File file);
	void setProperty(String name, String value);
	ConfigurationWrapper setProperties(Properties properties);
	void setEntityResolver(EntityResolver entityResolver);
	void setNamingStrategy(NamingStrategyWrapper namingStrategy);
	Properties getProperties();
	void addProperties(Properties properties);
	ConfigurationWrapper configure(Document document);
	ConfigurationWrapper configure(File file);
	ConfigurationWrapper configure();
	void addClass(Class<?> clazz);
	void buildMappings();
	SessionFactoryWrapper buildSessionFactory();
	Iterator<PersistentClassWrapper> getClassMappings();
	void setPreferBasicCompositeIds(boolean b);
	void setReverseEngineeringStrategy(RevengStrategyWrapper strategy);
	void readFromJDBC();
	PersistentClassWrapper getClassMapping(String string);
	NamingStrategyWrapper getNamingStrategy();
	EntityResolver getEntityResolver();
	Iterator<TableWrapper> getTableMappings();
	
}
