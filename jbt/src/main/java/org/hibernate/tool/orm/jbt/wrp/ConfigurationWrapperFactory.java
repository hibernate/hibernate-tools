package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;

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

	static interface ConfigurationWrapper extends Wrapper {
		@Override default PersistentClass getWrappedObject() { return (PersistentClass)this; }
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
}
