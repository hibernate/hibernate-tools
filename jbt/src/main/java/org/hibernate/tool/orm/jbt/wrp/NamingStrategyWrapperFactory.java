package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class NamingStrategyWrapperFactory {
	
	public static Object create(String className) {
		Object delegate = ReflectUtil.createInstance(className);
		return Proxy.newProxyInstance( 
				NamingStrategyWrapperFactory.class.getClassLoader(), 
				new Class[] { NamingStrategy.class , StrategyClassNameProvider.class }, 
				new NamingStrategyInvocationHandler(delegate));
	}
	
	private static class NamingStrategyInvocationHandler implements InvocationHandler {
		
		private Object delegate = null;
		
		private NamingStrategyInvocationHandler(Object delegate) {
			this.delegate = delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getStrategyClassName")) {
				return delegate.getClass().getName();
			} else {
			    return method.invoke(delegate, args);
			}
		}
		
	}
	
	static interface StrategyClassNameProvider {
		public String getStrategyClassName();
	}
	
}
