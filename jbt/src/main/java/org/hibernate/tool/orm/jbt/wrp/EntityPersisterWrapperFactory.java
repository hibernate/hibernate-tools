package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.persister.entity.EntityPersister;

public class EntityPersisterWrapperFactory {
	
	public static Object create(EntityPersister delegate) {
		return Proxy.newProxyInstance(
				EntityPersisterWrapperFactory.class.getClassLoader(), 
				new Class[] { EntityPersisterExtension.class }, 
				new EntityPersisterInvocationHandler(delegate));
	}
	
	public static interface EntityPersisterExtension extends EntityPersister {
		boolean isInstanceOfAbstractEntityPersister();
		Object getTuplizerPropertyValue(Object entity, int i);
		Integer getPropertyIndexOrNull(String propertyName);
	}
	
	private static class EntityPersisterInvocationHandler implements InvocationHandler {
		
		private EntityPersister delegate;
		
		private EntityPersisterInvocationHandler(EntityPersister delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("isInstanceOfAbstractEntityPersister".equals(method.getName())) {
				return true;
			} else if ("getTuplizerPropertyValue".equals(method.getName())) {
				return delegate.getValue(args[0], (int)args[1]);
			} else if ("getPropertyIndexOrNull".equals(method.getName())) {
				return delegate.getEntityMetamodel().getPropertyIndexOrNull((String)args[0]);
			} else {
				return method.invoke(delegate, args);
			}
		}	
	}
	
}
