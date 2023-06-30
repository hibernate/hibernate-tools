package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.persister.collection.CollectionPersister;

public class CollectionPersisterWrapperFactory {

	public static Object create(CollectionPersister delegate) {
		return Proxy.newProxyInstance(
				CollectionPersisterWrapperFactory.class.getClassLoader(), 
				new Class[] { CollectionPersisterWrapper.class }, 
				new CollectionPersisterInvocationHandler(delegate));
	}
	
	static interface CollectionPersisterWrapper extends CollectionPersister, Wrapper {}
	
	static class CollectionPersisterInvocationHandler implements InvocationHandler {
		
		private CollectionPersister delegate;
		
		private CollectionPersisterInvocationHandler(CollectionPersister delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("getWrappedObject".equals(method.getName())) {
				return delegate;
			} else if ("getElementType".equals(method.getName())) {
				return TypeWrapperFactory.createTypeWrapper(delegate.getElementType());
			} else {
				return method.invoke(delegate, args);
			}
		}

	}
	
}
