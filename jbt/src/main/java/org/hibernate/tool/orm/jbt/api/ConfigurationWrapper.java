package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public interface ConfigurationWrapper extends Wrapper {

	default String getProperty(String property) { return ((Configuration)getWrappedObject()).getProperty(property); }
	default ConfigurationWrapper addFile(File file) { ((Configuration)getWrappedObject()).addFile(file); return this; }
	default void setProperty(String name, String value) { ((Configuration)getWrappedObject()).setProperty(name, value); }
	default ConfigurationWrapper setProperties(Properties properties) { ((Configuration)getWrappedObject()).setProperties(properties); return this; }
	default void setEntityResolver(EntityResolver entityResolver) {
		Object wrappedObject = getWrappedObject();
		if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).setEntityResolver(entityResolver);
		if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).setEntityResolver(entityResolver);
		if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).setEntityResolver(entityResolver);
	}
	default void setNamingStrategy(NamingStrategy namingStrategy) {
		Object wrappedObject = getWrappedObject();
		if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).setNamingStrategy(namingStrategy);
		if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).setNamingStrategy(namingStrategy);
		if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).setNamingStrategy(namingStrategy);
	}
	default Properties getProperties() { return ((Configuration)getWrappedObject()).getProperties(); }
	default void addProperties(Properties properties) { ((Configuration)getWrappedObject()).addProperties(properties); }
	default ConfigurationWrapper configure(Document document) {
		Object wrappedObject = getWrappedObject();
		if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).configure(document);
		if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).configure(document);
		if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).configure(document);
		return this;		
	}
	default ConfigurationWrapper configure(File file) { ((Configuration)getWrappedObject()).configure(file); return this; }
	default ConfigurationWrapper configure() { ((Configuration)getWrappedObject()).configure(); return this; }
	default void addClass(Class<?> clazz) { ((Configuration)getWrappedObject()).addClass(clazz); }
	default void buildMappings() {
		Object wrappedObject = getWrappedObject();
		if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).buildMappings();
		if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).buildMappings();
		if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).buildMappings();
	}
	default SessionFactory buildSessionFactory() { return ((Configuration)getWrappedObject()).buildSessionFactory(); }
	default Iterator<PersistentClass> getClassMappings() { 
		Object wrappedObject = getWrappedObject();
		if (wrappedObject instanceof NativeConfiguration) return ((NativeConfiguration)wrappedObject).getClassMappings();
		if (wrappedObject instanceof RevengConfiguration) return ((RevengConfiguration)wrappedObject).getClassMappings();
		if (wrappedObject instanceof JpaConfiguration) return ((JpaConfiguration)wrappedObject).getClassMappings();
		return null;
	}
	
}
