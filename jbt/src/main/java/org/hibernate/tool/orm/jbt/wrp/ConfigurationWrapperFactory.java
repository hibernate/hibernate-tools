package org.hibernate.tool.orm.jbt.wrp;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationWrapperFactory {
	
	public static ConfigurationWrapper createNativeConfigurationWrapper() {
		return (ConfigurationWrapper)Proxy.newProxyInstance(
				ConfigurationWrapperFactory.class.getClassLoader(), 
				new Class[] { ConfigurationWrapper.class }, 
				new ConfigurationWrapperInvocationHandler(new NativeConfigurationWrapperImpl()));
	}
	
	public static ConfigurationWrapper createRevengConfigurationWrapper() {
		return (ConfigurationWrapper)Proxy.newProxyInstance(
				ConfigurationWrapperFactory.class.getClassLoader(), 
				new Class[] { ConfigurationWrapper.class }, 
				new ConfigurationWrapperInvocationHandler(new RevengConfigurationWrapperImpl()));
	}

	public static ConfigurationWrapper createJpaConfigurationWrapper(
			String persistenceUnit, Map<?, ?> properties) {
		return (ConfigurationWrapper)Proxy.newProxyInstance(
				ConfigurationWrapperFactory.class.getClassLoader(), 
				new Class[] { ConfigurationWrapper.class }, 
				new ConfigurationWrapperInvocationHandler(
						new JpaConfigurationWrapperImpl(persistenceUnit, properties)));
	}

	static interface ConfigurationWrapper extends Wrapper {
		@Override default Configuration getWrappedObject() { return (Configuration)this; }
		String getProperty(String name);
		Configuration addFile(File file);
		Configuration setProperty(String name, String value);
		Configuration setProperties(Properties properties);
		void setEntityResolver(EntityResolver testResolver);
		void setNamingStrategy(NamingStrategy namingStrategy);
		Properties getProperties();
		Configuration addProperties(Properties testProperties);
		Configuration configure(Document document);
		Configuration configure(File cfgXmlFile);
		Configuration configure();
		Configuration addClass(Class<?> class1);
		void buildMappings();
		SessionFactory buildSessionFactory();
		Iterator<PersistentClass> getClassMappings();
		void setPreferBasicCompositeIds(boolean b);
		void setReverseEngineeringStrategy(RevengStrategy reverseEngineeringStrategy);
	}
	
	static class ConfigurationWrapperInvocationHandler implements InvocationHandler {
		
		private ConfigurationWrapper configurationWrapper = null;
		
		public ConfigurationWrapperInvocationHandler(ConfigurationWrapper wrapper) {
			configurationWrapper = wrapper;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return method.invoke(configurationWrapper, args);
			} catch (InvocationTargetException t) {
				throw t.getTargetException();
			}
		}
		
	}
	
	static class NativeConfigurationWrapperImpl 
			extends NativeConfiguration 
			implements ConfigurationWrapper {
	}

	static class RevengConfigurationWrapperImpl 
			extends RevengConfiguration 
			implements ConfigurationWrapper {
	}

	static class JpaConfigurationWrapperImpl 
			extends JpaConfiguration 
			implements ConfigurationWrapper {
		public JpaConfigurationWrapperImpl(String persistenceUnit, Map<?, ?> properties) {
			super(persistenceUnit, properties);
		}
	}

}
