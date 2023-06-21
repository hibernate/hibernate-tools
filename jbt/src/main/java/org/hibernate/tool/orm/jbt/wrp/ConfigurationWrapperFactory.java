package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.orm.jbt.util.NativeConfiguration;

public class ConfigurationWrapperFactory {
	
	public static ConfigurationWrapper createNativeConfigurationWrapper() {
		return (ConfigurationWrapper)Proxy.newProxyInstance(
				ConfigurationWrapperFactory.class.getClassLoader(), 
				new Class[] { ConfigurationWrapper.class }, 
				new ConfigurationWrapperInvocationHandler(new NativeConfigurationWrapperImpl()));
	}
	
	static interface ConfigurationWrapper extends Wrapper {
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

}
