package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.api.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationWrapperFactory {

	public static ConfigurationWrapper createConfigurationWrapper(final Configuration wrappedConfiguration) {
		return new ConfigurationWrapperImpl(wrappedConfiguration);
	}
	
	private static class ConfigurationWrapperImpl implements ConfigurationWrapper {
		
		private Configuration wrappedConfiguration = null;
		
		private ConfigurationWrapperImpl(Configuration configuration) {
			wrappedConfiguration = configuration;
		}
		
		@Override 
		public Configuration getWrappedObject() { 
			return wrappedConfiguration; 
		}
		
		@Override
		public String getProperty(String property) { 
			return ((Configuration)getWrappedObject()).getProperty(property); 
		}
		
		@Override
		public ConfigurationWrapper addFile(File file) { 
			((Configuration)getWrappedObject()).addFile(file); return this; 
		}
		
		@Override
		public void setProperty(String name, String value) { 
			((Configuration)getWrappedObject()).setProperty(name, value); 
		}
		
		@Override
		public ConfigurationWrapper setProperties(Properties properties) { 
			((Configuration)getWrappedObject()).setProperties(properties); return this; 
		}
		
		@Override
		public void setEntityResolver(EntityResolver entityResolver) {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).setEntityResolver(entityResolver);
			if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).setEntityResolver(entityResolver);
			if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).setEntityResolver(entityResolver);
		}
		
		@Override
		public void setNamingStrategy(NamingStrategy namingStrategy) {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).setNamingStrategy(namingStrategy);
			if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).setNamingStrategy(namingStrategy);
			if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).setNamingStrategy(namingStrategy);
		}
		
		@Override
		public Properties getProperties() { 
			return ((Configuration)getWrappedObject()).getProperties(); 
		}
		
		@Override
		public void addProperties(Properties properties) { 
			((Configuration)getWrappedObject()).addProperties(properties); 
		}
		
		@Override
		public ConfigurationWrapper configure(Document document) {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).configure(document);
			if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).configure(document);
			if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).configure(document);
			return this;		
		}
		
		@Override
		public ConfigurationWrapper configure(File file) { 
			((Configuration)getWrappedObject()).configure(file); 
			return this; 
		}
		
		@Override
		public ConfigurationWrapper configure() { 
			((Configuration)getWrappedObject()).configure(); 
			return this; 
		}
		
		@Override
		public void addClass(Class<?> clazz) { 
			((Configuration)getWrappedObject()).addClass(clazz); 
		}
		
		@Override
		public void buildMappings() {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).buildMappings();
			if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).buildMappings();
			if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).buildMappings();
		}
		
		@Override
		public SessionFactory buildSessionFactory() { 
			return ((Configuration)getWrappedObject()).buildSessionFactory(); 
		}
		
		@Override
		public Iterator<PersistentClass> getClassMappings() { 
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) return ((NativeConfiguration)wrappedObject).getClassMappings();
			if (wrappedObject instanceof RevengConfiguration) return ((RevengConfiguration)wrappedObject).getClassMappings();
			if (wrappedObject instanceof JpaConfiguration) return ((JpaConfiguration)wrappedObject).getClassMappings();
			return null;
		}
		
		@Override
		public void setPreferBasicCompositeIds(boolean b) {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).setPreferBasicCompositeIds(b);
			if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).setPreferBasicCompositeIds(b);
			if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).setPreferBasicCompositeIds(b);
		}
		
		@Override
		public void setReverseEngineeringStrategy(RevengStrategy strategy) {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).setReverseEngineeringStrategy(strategy);
			if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).setReverseEngineeringStrategy(strategy);
			if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).setReverseEngineeringStrategy(strategy);
		}
		
		@Override
		public void readFromJDBC() {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).readFromJDBC();
			if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).readFromJDBC();
			if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).readFromJDBC();
		}
		
		@Override
		public PersistentClass getClassMapping(String string) { 
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) return ((NativeConfiguration)wrappedObject).getClassMapping(string);
			if (wrappedObject instanceof RevengConfiguration) return ((RevengConfiguration)wrappedObject).getClassMapping(string);
			if (wrappedObject instanceof JpaConfiguration) return ((JpaConfiguration)wrappedObject).getClassMapping(string);
			return null;
		}
		
		@Override
		public NamingStrategy getNamingStrategy() {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) return ((NativeConfiguration)wrappedObject).getNamingStrategy();
			if (wrappedObject instanceof RevengConfiguration) return ((RevengConfiguration)wrappedObject).getNamingStrategy();
			if (wrappedObject instanceof JpaConfiguration) return ((JpaConfiguration)wrappedObject).getNamingStrategy();
			return null;
		}
		
		@Override
		public EntityResolver getEntityResolver() {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) return ((NativeConfiguration)wrappedObject).getEntityResolver();
			if (wrappedObject instanceof RevengConfiguration) return ((RevengConfiguration)wrappedObject).getEntityResolver();
			if (wrappedObject instanceof JpaConfiguration) return ((JpaConfiguration)wrappedObject).getEntityResolver();
			return null;
		}
		
		@Override
		public Iterator<Table> getTableMappings() {
			Object wrappedObject = getWrappedObject();
			if (wrappedObject instanceof NativeConfiguration) return ((NativeConfiguration)wrappedObject).getTableMappings();
			if (wrappedObject instanceof RevengConfiguration) return ((RevengConfiguration)wrappedObject).getTableMappings();
			if (wrappedObject instanceof JpaConfiguration) return ((JpaConfiguration)wrappedObject).getTableMappings();
			return null;
		}
		
	}
	
}
