package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class ExporterWrapperFactory {
	
	public static ExporterWrapper create(String className) {
		Object delegate = ReflectUtil.createInstance(className);
		return (ExporterWrapper)Proxy.newProxyInstance( 
				ExporterWrapperFactory.class.getClassLoader(), 
				new Class[] { ExporterWrapper.class }, 
				new ExporterInvocationHandler(delegate));
	}
	
	private static class ExporterInvocationHandler implements InvocationHandler {
		
		private Object delegate = null;
		
		private ExporterInvocationHandler(Object delegate) {
			this.delegate = delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(delegate, args);
		}
		
	}
	
	static interface ExporterWrapper {}

}
