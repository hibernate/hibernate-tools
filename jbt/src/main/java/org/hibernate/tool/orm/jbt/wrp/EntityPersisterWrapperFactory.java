package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.persister.entity.EntityPersister;

public class EntityPersisterWrapperFactory {
	
	public static Object create(EntityPersister delegate) {
		return Proxy.newProxyInstance(
				EntityPersisterWrapperFactory.class.getClassLoader(), 
				new Class[] { EntityPersister.class, EntityPersisterExtension.class }, 
				new EntityPersisterInvocationHandler(delegate));
	}
	
	public static interface EntityPersisterExtension {
		boolean isInstanceOfAbstractEntityPersister();
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
			} else {
				return method.invoke(delegate, args);
			}
		}	
	}

}
