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
import org.hibernate.tool.orm.jbt.internal.util.ExtendedConfiguration;
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
			return wrappedConfiguration.getProperty(property); 
		}
		
		@Override
		public ConfigurationWrapper addFile(File file) { 
			wrappedConfiguration.addFile(file); return this; 
		}
		
		@Override
		public void setProperty(String name, String value) { 
			wrappedConfiguration.setProperty(name, value); 
		}
		
		@Override
		public ConfigurationWrapper setProperties(Properties properties) { 
			wrappedConfiguration.setProperties(properties); return this; 
		}
		
		@Override
		public void setEntityResolver(EntityResolver entityResolver) {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				((ExtendedConfiguration)wrappedConfiguration).setEntityResolver(entityResolver);
			}
		}
		
		@Override
		public void setNamingStrategy(NamingStrategy namingStrategy) {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				((ExtendedConfiguration)wrappedConfiguration).setNamingStrategy(namingStrategy);
			}
		}
		
		@Override
		public Properties getProperties() { 
			return wrappedConfiguration.getProperties(); 
		}
		
		@Override
		public void addProperties(Properties properties) { 
			wrappedConfiguration.addProperties(properties); 
		}
		
		@Override
		public ConfigurationWrapper configure(Document document) {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				((ExtendedConfiguration)wrappedConfiguration).configure(document);
			}
			return this;		
		}
		
		@Override
		public ConfigurationWrapper configure(File file) { 
			wrappedConfiguration.configure(file); 
			return this; 
		}
		
		@Override
		public ConfigurationWrapper configure() { 
			wrappedConfiguration.configure(); 
			return this; 
		}
		
		@Override
		public void addClass(Class<?> clazz) { 
			wrappedConfiguration.addClass(clazz); 
		}
		
		@Override
		public void buildMappings() {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				((ExtendedConfiguration)wrappedConfiguration).buildMappings();
			}
		}
		
		@Override
		public SessionFactory buildSessionFactory() { 
			return ((Configuration)getWrappedObject()).buildSessionFactory(); 
		}
		
		@Override
		public Iterator<PersistentClass> getClassMappings() { 
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				return ((ExtendedConfiguration)wrappedConfiguration).getClassMappings();
			}
			return null;
		}
		
		@Override
		public void setPreferBasicCompositeIds(boolean b) {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				((ExtendedConfiguration)wrappedConfiguration).setPreferBasicCompositeIds(b);
			}
		}
		
		@Override
		public void setReverseEngineeringStrategy(RevengStrategy strategy) {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				((ExtendedConfiguration)wrappedConfiguration).setReverseEngineeringStrategy(strategy);
			}
		}
		
		@Override
		public void readFromJDBC() {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				((ExtendedConfiguration)wrappedConfiguration).readFromJDBC();
			}
		}
		
		@Override
		public PersistentClass getClassMapping(String string) { 
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				return ((ExtendedConfiguration)wrappedConfiguration).getClassMapping(string);
			}
			return null;
		}
		
		@Override
		public NamingStrategy getNamingStrategy() {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				return ((ExtendedConfiguration)wrappedConfiguration).getNamingStrategy();
			}
			return null;
		}
		
		@Override
		public EntityResolver getEntityResolver() {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				return ((ExtendedConfiguration)wrappedConfiguration).getEntityResolver();
			}
			return null;
		}
		
		@Override
		public Iterator<Table> getTableMappings() {
			if (wrappedConfiguration instanceof ExtendedConfiguration) {
				return ((ExtendedConfiguration)wrappedConfiguration).getTableMappings();
			}
			return null;
		}
		
	}
	
}
