package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class NamingStrategyWrapper {
	
	public static Object create(String className) {
		Object delegate = ReflectUtil.createInstance(className);
		return Proxy.newProxyInstance( 
				NamingStrategyWrapper.class.getClassLoader(), 
				new Class[] { NamingStrategy.class , ClassNameProvider.class }, 
				new NamingStrategyInvocationHandler(delegate));
	}
	
	private static class NamingStrategyInvocationHandler implements InvocationHandler {
		
		private Object delegate = null;
		
		private NamingStrategyInvocationHandler(Object delegate) {
			this.delegate = delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getClassName")) {
				return delegate.getClass().getName();
			} else {
			    return method.invoke(delegate, args);
			}
		}
		
	}
	
	static interface ClassNameProvider {
		public String getClassName();
	}
	
}
